package com.example.lab3.web.exception

class NotFoundException(message: String) : RuntimeException(message)
class ValidationException(message: String) : RuntimeException(message)