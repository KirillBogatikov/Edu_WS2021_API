from django.db import models
from django.db.models import CASCADE, Manager

from rest import validation


class Auth(models.Model):
    id = models.CharField(max_length=validation.IDLength, primary_key=True)
    login = models.CharField(max_length=validation.Login.max_length, unique=True, db_index=True)
    password = models.CharField(max_length=validation.Password.max_length)
    objects = Manager()


class PersonalData(models.Model):
    id = models.CharField(max_length=validation.IDLength, primary_key=True)
    first_name = models.CharField(max_length=validation.Name.max_length)
    last_name = models.CharField(max_length=validation.Name.max_length)
    patronymic = models.CharField(max_length=validation.Name.max_length, null=True)
    phone = models.CharField(max_length=validation.Phone.max_length)
    email = models.CharField(max_length=validation.Email.max_length)
    objects = Manager()


class User(models.Model):
    id = models.CharField(max_length=validation.IDLength, primary_key=True)
    personal_data = models.ForeignKey(PersonalData, on_delete=CASCADE)
    auth = models.ForeignKey(Auth, on_delete=CASCADE)
    objects = Manager()


class Issue(models.Model):
    id = models.CharField(max_length=validation.IDLength, primary_key=True)
    user = models.ForeignKey(User, on_delete=CASCADE)
    pet_name = models.CharField(max_length=validation.Name.max_length, db_index=True)
    pet_photo = models.CharField(max_length=validation.IDLength)
    result_photo = models.CharField(max_length=validation.IDLength)
    objects = Manager()
