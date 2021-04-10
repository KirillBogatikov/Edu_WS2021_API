import base64
import hashlib
import hmac
import json
import time
import uuid

from django.conf.global_settings import SECRET_KEY
from rest_framework.authentication import BaseAuthentication
from rest_framework.exceptions import AuthenticationFailed


def sha256_encode(message):
    return hmac.new(SECRET_KEY.encode(), message.encode(), hashlib.sha256).hexdigest().encode()


def sha256_decode(message):
    return hmac.new(SECRET_KEY.encode(), message.encode(), hashlib.sha256)


def b64_encode(value):
    return base64.b64encode(json.dumps(value).encode()).decode('utf-8')


def b64_decode(value):
    return json.loads(base64.b64decode(value).decode('utf-8'))


def signature(data):
    return base64.b64encode(sha256_encode(data))


class JWToken:
    def __init__(self, user_id, iat=time.time(), exp=None, jti=uuid.uuid4()):
        self.user_id = str(user_id)
        self.iat = iat
        self.exp = int(exp if exp is not None else iat + 7 * 24 * 60 * 60 * 1000)
        self.jti = str(jti)

    def unsigned(self):
        header = {"alg": "HS256", "typ": "JWT"}
        payload = {"user_id": self.user_id, "iat": self.iat, "exp": self.exp, "jti": self.jti}
        return "{}.{}".format(b64_encode(header), b64_encode(payload))

    def encode(self):
        data = self.unsigned()
        return "{}.{}".format(data, signature(data).decode('utf-8'))

    @staticmethod
    def decode(key):
        parts = key.split(".")

        payload = b64_decode(parts[1])
        token = JWToken(payload['user_id'], payload['iat'], payload['exp'], payload['jti'])
        if not signature(token.unsigned()) == parts[2].encode('utf-8'):
            raise PermissionError("Signature invalid")

        return token


class BearerAuth(BaseAuthentication):
    def authenticate(self, request):
        token = request.headers.get('Authorization')
        if token is None or not token.split(" ")[0] == "Bearer":
            raise AuthenticationFailed()
        else:
            jwt = JWToken.decode(token.split(" ")[1])
            return None, jwt
