package sk.momosilabs.suac.server.dashboard.service.startCharging

import sk.momosilabs.suac.server.common.GlobalUnprocessableException

class UserNotAllowedToChargeException() :
    GlobalUnprocessableException(userMessage = "User is not allowed to charge")
class CableNotRecognizedException() :
    GlobalUnprocessableException(userMessage = "Cable/Car is not connected")
class StartChargingFailureException() :
    GlobalUnprocessableException(userMessage = "Charging was not initiated or there was timeout waiting for result.")
