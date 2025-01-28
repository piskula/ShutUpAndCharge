package sk.momosilabs.suac.server.dashboard.service.stopCharging

import sk.momosilabs.suac.server.common.GlobalUnprocessableException
import sk.momosilabs.suac.server.dashboard.model.charging.external.CarStateEnum

class ChargingNotOngoingException(carState: CarStateEnum?) :
    GlobalUnprocessableException(userMessage = "Charging is not ongoing, but $carState")
class ChargingDoesNotBelongToUserException() :
    GlobalUnprocessableException(userMessage = "You do not have permission to stop this charging")
