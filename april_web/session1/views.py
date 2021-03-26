import json
from uuid import uuid4

from django.http import HttpResponse, HttpResponseRedirect, HttpResponseBadRequest, HttpResponseForbidden
from django.shortcuts import render, redirect

from session1.forms import LoginForm, SignupForm
from session1.models import User, AuthData, Role, Issue
from session1.utils import hex_hash, set_cookie


def main_page_view(request):
    return render(request, "index.html", {'login_form': LoginForm(), 'signup_form': SignupForm()})


def login_view(request):
    form = LoginForm(request.POST)
    try:
        form.is_valid()

        auth = AuthData.objects.get(login=form.cleaned_data['login'])
        if hex_hash(form.cleaned_data['password']) == auth.password:
            resp = HttpResponseRedirect("/")
            set_cookie(resp, "auth_id", auth.id, 1)
            return resp

        return HttpResponseBadRequest()
    except ValueError as e:
        return redirect("/?status=failure")


def signup_view(request):
    form = SignupForm(request.POST)
    try:
        form.is_valid()

        auth = AuthData.objects.create(
            id=str(uuid4()),
            login=form.cleaned_data['login'],
            password=hex_hash(form.cleaned_data['password']),
            role=Role.user.value
        )

        User.objects.create(
            id=str(uuid4()),
            auth=auth,
            first_name=form.cleaned_data['first_name'],
            last_name=form.cleaned_data['last_name'],
            patronymic=form.cleaned_data['patronymic'],
            email=form.cleaned_data['email']
        ).save()

        return redirect("/?status=signup_ok")
    except ValueError as e:
        return redirect("/?status=failure")


def list_issues_view(request):
    auth_id = request.COOKIES.get("auth_id")
    if auth_id is None:
        return HttpResponseForbidden()

    issues = []
    try:
        user_id = User.objects.get(auth = auth_id).id
        issues = Issue.objects.filter(user = user_id)[::1]
        issues = [i.as_json() for i in issues]
    except Exception as e:
        print(e)
        pass

    return HttpResponse(json.dumps(issues), content_type='application/json')