import json
import re
from uuid import uuid4

from django.conf import settings
from django.core.exceptions import ObjectDoesNotExist
from django.http import HttpResponseNotFound, HttpResponse, HttpResponseForbidden
from django.shortcuts import render

from base.models import MinLoginLength, MaxLoginLength, MinNameLength, MaxNameLength, MinPasswordLength, \
    MaxPasswordLength, User, Auth, MinEmailLength, MaxEmailLength, Bio, Role
from base.utils import hex_hash
from user.views import list_last_issues


def index(request):
    if request.COOKIES.get("user_id") is not None:
        return list_last_issues(request)

    return render(request, "index.html", {
        "login_min": MinLoginLength,
        "login_max": MaxLoginLength,
        "password_min": MinPasswordLength,
        "password_max": MaxPasswordLength,
        "first_name_min": MinNameLength,
        "first_name_max": MaxNameLength,
        "last_name_min": MinNameLength,
        "last_name_max": MaxNameLength,
        "patronymic_min": MinNameLength,
        "patronymic_max": MaxNameLength,
        "email_min": MinEmailLength,
        "email_max": MaxEmailLength,
        "authorized": False
    })


def validate(value, regex, min, max):
    return len(value) < min or len(value) > max or not re.match(regex, value)


def logout(request):
    r = HttpResponse()
    r.set_cookie("user_id", "", -24 * 60 * 60, domain=settings.SESSION_COOKIE_DOMAIN)
    r.set_cookie("login", "", -24 * 60 * 60, domain=settings.SESSION_COOKIE_DOMAIN)
    return r


def signup(request):
    try:
        body = json.loads(request.body)

        login = body['login']
        password = body['password']
        first_name = body['first_name']
        last_name = body['last_name']
        patronymic = body['patronymic']
        email = body['email']

        errors = {
            'login': validate(login, r"^[a-zA-Z0-9_]+$", MinLoginLength, MaxLoginLength),
            'password': validate(password, r"^[a-zA-Z0-9_#%&@]+$", MinPasswordLength, MaxPasswordLength),
            'first_name': validate(first_name, r"^[a-zA-Zа-яА-Я ]+$", MinNameLength, MaxNameLength),
            'last_name': validate(last_name, r"^[a-zA-Zа-яА-Я ]+$", MinNameLength, MaxNameLength),
            'patronymic': validate(patronymic, r"^[a-zA-Zа-яА-Я ]+$", MinNameLength, MaxNameLength),
            'email': validate(email, r"^\w+@\w+\.\w+$", MinEmailLength, MaxEmailLength),
        }

        if True in errors.keys():
            return HttpResponse(status=400, content=json.dumps(errors), content_type="application/json")

        password_hash = hex_hash(password)
        auth = Auth.objects.create(login=login, password=password_hash, role=Role.User.value)
        bio = Bio.objects.create(id=uuid4(), first_name=first_name, last_name=last_name,
                                 patronymic=patronymic, email=email)
        user = User.objects.create(id=uuid4(), auth=auth, bio=bio)
        try:
            user.save()
        except Exception as e:
            print(e)
            return HttpResponse(status=500)

        # 201
        return HttpResponse(status=201)
    except ObjectDoesNotExist:
        # 404
        return HttpResponseNotFound()


def login(request):
    try:
        body = json.loads(request.body)

        user = User.by_login(body['login'])
        if user.auth.password == hex_hash(body['password']):
            # 200
            r = HttpResponse()
            r.set_cookie("user_id", user.id, 24 * 60 * 60, domain=settings.SESSION_COOKIE_DOMAIN)
            r.set_cookie("login", user.auth.login, 24 * 60 * 60, domain=settings.SESSION_COOKIE_DOMAIN)
            return r

        # 403
        return HttpResponseForbidden()
    except ObjectDoesNotExist:
        # 404
        return HttpResponseNotFound()
