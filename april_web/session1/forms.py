import re

from django.core.exceptions import ValidationError
from django.forms import Form, CharField, PasswordInput

from session1.models import LOGIN_LENGTH, BIO_LENGTH, EMAIL_LENGTH


class LoginField(CharField):
    def validate(self, value):
        super().validate(value)
        if not re.match(r'^[a-zA-ZА-Яа-я ]+$', value):
            ValidationError(format('Invalid value: %s', value))


class PasswordField(CharField):
    def validate(self, value):
        super().validate(value)
        if not re.match(r'^[a-zA-ZА-Яа-я0-9!@#&-+= ]+$', value):
            ValidationError(format('Invalid value: %s', value))


class EmailField(CharField):
    def validate(self, value):
        super().validate(value)
        if not re.match(r'[a-zA-Z0-9]+@[a-zA-Z0-9]+[.][a-zA-Z0-9]+', value):
            ValidationError(format('Invalid value: %s', value))


class LoginForm(Form):
    login = LoginField(label="Логин", max_length=LOGIN_LENGTH)
    password = PasswordField(widget=PasswordInput(), label="Пароль")


class SignupForm(Form):
    login = LoginField(label="Логин", max_length=LOGIN_LENGTH)
    password = PasswordField(widget=PasswordInput(), label="Пароль")
    email = EmailField(label="E-mail", max_length=EMAIL_LENGTH)
    last_name = CharField(label="Фамилия", max_length=BIO_LENGTH)
    first_name = CharField(label="Имя", max_length=BIO_LENGTH)
    patronymic = CharField(label="Отчество", max_length=BIO_LENGTH)
