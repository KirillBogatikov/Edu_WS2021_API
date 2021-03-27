from uuid import uuid4

from django.http import HttpResponse
from django.shortcuts import render

from april_web.settings import BASE_DIR
from base.models import User, Issue


def get_issues(id):
    try:
        user = User.objects.get(id=id)
        issues = Issue.objects.filter(user=user)[::1]
        return issues
    except Exception as e:
        print(e)
        return []


def list_last_issues(request):
    issues = get_issues(request.COOKIES.get("user_id"))
    return render(request, "issues.html",
                  {'issues_count': len(issues), 'last_issues': issues, 'workplace': False, 'authorized': True})


def get_issue_photo(request, w):
    with open("{0}/issue_photo/{1}.png".format(BASE_DIR, w), "rb") as f:
        return HttpResponse(status=200, content=f.read(), content_type="image/png")


def user_page(request):
    issues = get_issues(request.COOKIES.get("user_id"))
    return render(request, "issues.html",
                  {'issues_count': len(issues), 'last_issues': issues, 'workplace': True, 'authorized': True})


def create_issue(request):
    user = User.objects.get(id=request.COOKIES.get("user_id"))

    photo_id = uuid4()
    print()
    with open("{0}/issue_photo/{1}.png".format(BASE_DIR, str(photo_id)), "wb") as f:
        f.write(request.FILES.get("photo").read())

    issue = Issue.objects.create(id=uuid4(), user=user, name=request.POST.get("name"), pet_photo=photo_id)
    issue.save()

    # 200
    return HttpResponse(status=200)
