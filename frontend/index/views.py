from django.shortcuts import render
from django.http import HttpResponseRedirect

def redirect(request):
    return HttpResponseRedirect("/reservations/")
