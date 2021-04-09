import json

from django.http import HttpResponseNotFound, HttpResponse
from django.shortcuts import render

from base.models import Issue


def index(request):
    issues = Issue.objects.all()[::1]
    return render(request, "groom.html", {'authorized': True, 'issues_count': len(issues), 'issues': issues})


def find_issue(request, id):
    try:
        return HttpResponse(status=200, content=json.dumps(Issue.objects.get(id=id).as_json()).encode('utf-8'))
    except Exception as e:
        print(e)
        return HttpResponseNotFound()
