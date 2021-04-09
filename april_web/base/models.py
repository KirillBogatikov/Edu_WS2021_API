from enum import Enum

from django.db.models import CharField, IntegerField, Model, ForeignKey, CASCADE, Manager

IdLength = 36  # uuid length 32 + 4 dishes


class Role(Enum):
    User = 0
    Admin = 1


MinLoginLength = 4
MaxLoginLength = 32
MinPasswordLength = 4
MaxPasswordLength = 64
HashLength = 65 # sha-256 hex-digest


class Auth(Model):
    login = CharField(max_length=MaxLoginLength, primary_key=True)
    password = CharField(max_length=HashLength)
    role = IntegerField()
    objects = Manager()

    def as_json(self):
        return dict(
            login=self.login,
            password=self.password,
            role=self.role
        )


MinNameLength = 2
MaxNameLength = 256
MinEmailLength = 5
MaxEmailLength = 256


class Bio(Model):
    id = CharField(max_length=IdLength, primary_key=True)
    first_name = CharField(max_length=MaxNameLength)
    last_name = CharField(max_length=MaxNameLength)
    patronymic = CharField(max_length=MaxNameLength)
    email = CharField(max_length=MaxEmailLength)
    objects = Manager()

    def as_json(self):
        return dict(
            id=self.id,
            first_name=self.first_name,
            last_name=self.last_name,
            patronymic=self.patronymic,
            email=self.email
        )


class User(Model):
    id = CharField(max_length=IdLength, primary_key=True)
    auth = ForeignKey(Auth, on_delete=CASCADE)
    bio = ForeignKey(Bio, on_delete=CASCADE)
    objects = Manager()

    @classmethod
    def by_login(cls, login):
        auth = Auth.objects.get(login=login)
        return cls.objects.get(auth=auth)

    def as_json(self):
        return dict(
            id=self.id,
            auth=self.auth.as_json(),
            bio=self.bio.as_json()
        )


class IssueStatus(Enum):
    New = 0
    Processing = 1
    Ready = 2


MinPetName = 1
MaxPetName = 256


class Issue(Model):
    id = CharField(max_length=IdLength, primary_key=True)
    user = ForeignKey(User, on_delete=CASCADE)
    name = CharField(max_length=MaxPetName)
    pet_photo = CharField(max_length=IdLength)
    result_photo = CharField(max_length=IdLength)
    status = IntegerField()
    objects = Manager()

    def as_json(self):
        return dict(
            id=self.id,
            user=self.user.as_json(),
            name=self.name,
            pet_photo=self.pet_photo,
            result_photo=self.result_photo,
            status=self.status
        )
