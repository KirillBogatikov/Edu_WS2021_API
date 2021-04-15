import base64
import hashlib
import hmac
import json
import time
from uuid import uuid4

from rest_framework.authentication import BaseAuthentication
from rest_framework.exceptions import AuthenticationFailed

from april_web.settings import SECRET_KEY
from rest.models import User


class Hash:
    __secret_key__ = SECRET_KEY.encode("utf-8")
    __alg__ = hashlib.sha256

    @staticmethod
    def hash_mac_of(value, hex=False):
        hm = hmac.new(Hash.__secret_key__, value, Hash.__alg__)
        return hm.hexdigest() if hex else hm.digest()

    @staticmethod
    def hash_of(value):
        return Hash.__alg__(value.encode('utf-8')).hexdigest()


class Base64:
    @staticmethod
    def encode(value, to_string=True):
        if type(value) is dict:
            value = json.dumps(value).encode('utf-8')

        r = base64.b64encode(value)
        return r.decode('utf-8') if to_string else r

    @staticmethod
    def decode(value, to_string=True, to_json=False):
        r = base64.b64decode(value)
        if to_json:
            return json.loads(r)

        if to_string:
            return r.decode('utf-8')

        return r


class JWToken():
    TokenLifeTime = 1 * 24 * 60 * 60 * 1000  # one day in ms

    def __init__(self, user_id, user=None, iat=None, exp=None, jti=None):
        self.user_id = str(user_id)
        self.user = user
        self.iat = round(time.time() if iat is None else iat)
        self.exp = round(self.iat + JWToken.TokenLifeTime if exp is None else exp)
        self.jti = str(uuid4()) if jti is None else jti

    def encode(self):
        header = {'typ': 'JWT', 'alg': 'HS256'}
        payload = self.__dict__
        # remove user personal data from token object
        payload.pop('user')

        unsigned = "{}.{}".format(Base64.encode(header), Base64.encode(payload))
        return "{}.{}".format(unsigned, JWToken.sign(unsigned))

    @staticmethod
    def sign(header, payload=None):
        if payload is not None and header is not None:
            unsigned = "{}.{}".format(header, payload)
        else:
            unsigned = header
        return Base64.encode(Hash.hash_mac_of(unsigned.encode('utf-8')))

    @staticmethod
    def decode(value):
        parts = value.split(".")
        if not JWToken.sign(parts[0], parts[1]) == parts[2]:
            raise AuthenticationFailed("Invalid signature")

        payload = Base64.decode(parts[1], to_json=True)

        token = JWToken(user_id=payload['user_id'], iat=int(payload['iat']), exp=int(payload['exp']),
                        jti=payload['jti'])
        if token.exp <= time.time():
            raise AuthenticationFailed("Token is expired")

        return token


class BearerAuth(BaseAuthentication):
    def authenticate(self, request):
        header = request.headers.get('Authorization', "")
        parts = header.split(" ")
        if len(parts) < 2 or not parts[0] == "Bearer":
            raise AuthenticationFailed("Invalid token")

        token = JWToken.decode(parts[1])
        try:
            token.user = User.objects.get(id=token.user_id)
        except Exception as e:
            print("User not found: ", e)
            raise AuthenticationFailed("No user with id '{}'".format(token.user_id))

        return None, token
