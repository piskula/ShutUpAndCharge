package sk.momosilabs.suac.server.dashboard.service.setChargingParams

import sk.momosilabs.suac.server.common.GlobalUnprocessableException
import sk.momosilabs.suac.server.dashboard.model.charging.external.CarStateEnum

class ChargingNotInitiatedException(carState: CarStateEnum?) :
    GlobalUnprocessableException(userMessage = "Charging is not initiated, but $carState")
class ChargingDoesNotBelongToUserException() :
    GlobalUnprocessableException(userMessage = "You do not have permission to change parameters of this charging")
