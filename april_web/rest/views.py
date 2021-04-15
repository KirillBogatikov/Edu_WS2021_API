import json
import sqlite3
from uuid import uuid4

from django.core.serializers import serialize, deserialize
from rest_framework.decorators import api_view, permission_classes, authentication_classes
from rest_framework.exceptions import AuthenticationFailed, APIException, ParseError
from rest_framework.permissions import BasePermission
from rest_framework.response import Response

from april_web.settings import DATABASES
from rest.auth import Hash, JWToken, BearerAuth
from rest.models import Role, User, Auth, PersonalData
from rest.serialization import UserSerializer
from rest.validation import LoginRule, PasswordRule, NameRule, EmailRule, PhoneRule


class IsAuthorized(BasePermission):
    def has_permission(self, request, view):
        return request.auth is not None


class IsAdmin(BasePermission):
    def has_permission(self, request, view):
        return request.auth is not None and \
               request.auth.user is not None and \
               request.auth.user.auth.role == Role.Admin.value


@api_view(['POST'])
@authentication_classes([])
@permission_classes([])
def login(request):
    data = json.loads(request.body)

    try:
        auth = Auth.objects.get(login=data['login'])
    except Exception:
        print("User not found")
        raise AuthenticationFailed("User not found")

    password_hash = Hash.hash_of(data['password'])
    if not password_hash == auth.password:
        print("Password incorrect")
        raise AuthenticationFailed("Password incorrect")

    try:
        user = User.objects.get(auth=auth)
        token = JWToken(user.id).encode()
        return Response(status=200, data=json.dumps({'token': token }))
    except Exception as e:
        print("Token generation failed ", e)
        # haha, works on it, haha
        raise APIException("Unknown error. Don't worry, our specialist already works on it")


@api_view(['POST'])
@authentication_classes([])
@permission_classes([])
def signup(request):
    auth = None
    data = json.loads(request.body)

    try:
        LoginRule.validate(data['login'], 'login')
        PasswordRule.validate(data['password'], 'password')
        NameRule.validate(data['first_name'], 'first_name')
        NameRule.validate(data['last_name'], 'last_name')
        if data['patronymic'] is not None:
            NameRule.validate(data['patronymic'], 'patronymic')

        PhoneRule.validate(data['phone'], 'phone')
        EmailRule.validate(data['email'], 'email')
    except ValueError as e:
        raise ParseError(e)
    except KeyError as e:
        raise ParseError("{} is required".format(e))

    try:
        auth = Auth.objects.get(login=data['login'])
    except Exception:
        pass

    if auth is not None:
        raise ParseError("User already exists")

    auth = Auth.objects.create(id=uuid4(), login=data['login'], password=Hash.hash_of(data['password']),
                               role=Role.User.value)
    personal = PersonalData.objects.create(id=uuid4(), first_name=data['first_name'], last_name=data['last_name'],
                                           patronymic=data['patronymic'], phone=data['phone'], email=data['email'])

    try:
        auth.save()
        personal.save()
        User.objects.create(id=uuid4(), auth=auth, personal=personal).save()
    except Exception as e:
        print("User save failed ", e)
        raise APIException("Unknown error. Don't worry, our specialist already works on it")

    return Response(status=201)


@api_view(['GET'])
@authentication_classes([BearerAuth])
@permission_classes([IsAdmin])
def list_users(request):
    offset = request.GET.get('offset', 0)
    limit = request.GET.get('limit', 100)

    con = sqlite3.connect(DATABASES['default']['NAME'])
    con.row_factory = sqlite3.Row
    cur = con.cursor()
    cur.execute("SELECT * FROM rest_user as ru JOIN rest_auth as ra ON ra.id = ru.auth_id JOIN rest_personal")
    list = cur.fetchall()
    for row in list:
        print(row['id'], row['login'])

    return Response(content_type='application/json')
