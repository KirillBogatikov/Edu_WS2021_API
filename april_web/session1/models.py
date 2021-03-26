from enum import Enum

from django.db.models import CharField, ForeignKey, CASCADE, IntegerField, Model, Manager


class Role(Enum):
    user = 1
    admin = 2


UUID_LENGTH = 36
LOGIN_LENGTH = 16
HASH_LENGTH = 65


class AuthData(Model):
    id = CharField(max_length=UUID_LENGTH, primary_key=True)
    login = CharField(max_length=LOGIN_LENGTH, unique=True)
    role = IntegerField()
    password = CharField(max_length=HASH_LENGTH)
    objects = Manager()


BIO_LENGTH = 256
EMAIL_LENGTH = 256


class User(Model):
    id = CharField(max_length=UUID_LENGTH, primary_key=True)
    auth = ForeignKey(AuthData, on_delete=CASCADE)
    first_name = CharField(max_length=BIO_LENGTH)
    last_name = CharField(max_length=BIO_LENGTH)
    email = CharField(max_length=EMAIL_LENGTH)
    patronymic = CharField(max_length=BIO_LENGTH)
    objects = Manager()


PET_NAME_LENGTH = 256


class Issue(Model):
    id = CharField(max_length=UUID_LENGTH, primary_key=True)
    user = ForeignKey(User, on_delete=CASCADE)
    pet_name = CharField(max_length=PET_NAME_LENGTH)
    pet_photo = CharField(max_length=UUID_LENGTH)
    objects = Manager()

    def as_json(self):
        return dict(
            id=self.id,
            name=self.pet_name,
            photo=self.pet_photo)

    def all(self, user=None):
        result = []

        try:
            if user is None:
                result = self.objects.all()
            else:
                result = self.objects.filter(user=user)[::1]
        except Exception as e:
            print(e)

        return [i.as_json() for i in result]

