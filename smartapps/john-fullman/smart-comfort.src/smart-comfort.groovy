/**
 *  Smart Comfort
 *
 *  Controls your home's climate integrating as many devices and data points as possible.
 *    -Supports up to 3 rooms
 *    -Supports away, home and sleeping use cases
 *    -Averages house temperature based on which rooms are occupied
 *    -Detects occupancy via motion, switches or location mode
 *    -Controls heaters and air conditioners in specific rooms that coordinate with central air
 *     (for drafty rooms or rooms without vents)
 *
 *  Copyright 2015 John Fullman
 *  GNU General Public License v2 (https://www.gnu.org/licenses/gpl-2.0.txt)
 *
 */
definition(
    name: "Smart Comfort",
    namespace: "john.fullman",
    author: "John Fullman",
    description: "Controls your home's climate integrating as many devices and data points as possible.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo@2x.png")

preferences
{
	page(name: "RoomCountPage", title: "Welcome to Smart Comfort", nextPage: "OverviewPage", install: false, uninstall: true)
    {
    	section("Instructions")
        {
        	paragraph "Houses have more than one room. Some houses have no central heating or cooling and only have space heaters and air conditioners in each room."
            paragraph "Smart Comfort will manage your temperature however you like it in whatever rooms it detects that you are occupying."
        }
        section("Rooms")
        {
        	paragraph "A room must at least have a temperature sensor or thermostat. Ideally, a room would have other sensors and actuators like a motion sensor, light switch, space heater or air conditioner."
        	input "RoomCount", "enum", title: "Number of Managed Rooms:", required: true, options: ["0", "1", "2", "3"]
        }
    }
	page(name: "OverviewPage")
	page(name: "RoomPage1")
	page(name: "RoomPage2")
	page(name: "RoomPage3")
    page(name: "CompletePage", title: "Complete", install: true, uninstall: false)
    {
    	section("Instructions")
        {
        	paragraph "Configuration is complete. Click the install button to finish."
        }
    }
}

def GetNextPage(current_page_index)
{
	int iRoomCount = RoomCount.toInteger()
	int next_page_index = current_page_index + 1
    if(iRoomCount >= next_page_index)
    {
    	return "RoomPage$next_page_index"
    }
    else
    {
    	return "CompletePage"
    }
}

def OverviewPage()
{
	dynamicPage(name: "OverviewPage", title: "Overall Configuration", nextPage: GetNextPage(0), install: false, uninstall: true)
    {
		section("Thermostat")
        {
        	paragraph "If you don't have a thermostat, Smart Comfort will coordinate your space heaters and air conditioners to keep your home comfortible."
			input "Thermostat", "capability.thermostat", title: "My thermostat:", required: false
            input "ThermostatMode", "enum", title: "Central thermostat abilities:", required: false, options: ["Heating and Cooling", "Heating Only", "Cooling Only"], defaultValue: "Heating and Cooling"
		}
        section("Desired Temperatures")
        {
			input "ComfortTemperature", "number", title: "Comfort temperature (degrees):", required: true, defaultValue: 72
			input "SleepingMinTemperature", "number", title: "Sleeping min temperature (degrees):", required: true, defaultValue: 60
            input "SleepingMaxTemperature", "number", title: "Sleeping max temperature (degrees):", required: true, defaultValue: 72
			input "AwayMinTemperature", "number", title: "Away min temperature (degrees):", required: true, defaultValue: 60
			input "AwayMaxTemperature", "number", title: "Away max temperature (degrees):", required: true, defaultValue: 85
        }
        section("Programming")
        {
        	input "SleepingModes", "mode", title: "Sleeping modes:", required: false, multiple: true
            input "SleepingStartTime", "time", title: "Bedtime:", required: false
            input "SleepingEndTime", "time", title: "Wakeup time:", required: false
        	input "AwayModes", "mode", title: "Away modes:", required: false, multiple: true
        }
    }
}

def RoomPage1()
{
    dynamicPage(name: "RoomPage1", title: "Room 1", nextPage: GetNextPage(1), install: false, uninstall: false)
    {
        section("Room Info")
        {
        	paragraph "You should enter rooms starting with the room you spend the most time in and work toward the least utilized room."
            paragraph "You must enter a temperature sensor to manage this room."
			input "RoomName1", "text", title: "Room Name:", required: false
            paragraph "The system needs to know if the room has a vent that connects to your thermostat's central air system."
            input "HasVent1", "enum", title: "Has Vents:", required: false, options: ["Yes", "No"]
        }
        section("Tempareature")
        {
        	paragraph "Temperature sensors will average to tell the app what the temperature of the room is."
        	input "TemperatureSensors1", "capability.temperatureMeasurement", title: "Temperature sensor:", required: false, multiple: true
        	paragraph "If you find that the reported temperature is not accurate, you can offset it."
            input "TemperatureOffset1", "number", title: "Temperature sensor offset (degrees):", required: false
            input "TemperatureOffsetDirection1", "enum", title: "Temperature offset direction:", required: false, options: ["Up", "Down"]
        }
    	section("Occupancy")
        {
            paragraph "In these modes, this room will be considered occupied even if no motion is detected and no lights are on."
			input "RoomModes1", "mode", title: "Occupied modes:", required: false, multiple: true
        	paragraph "The room will be considered occupied while there is motion."
        	input "MotionSensors1", "capability.motionSensor", title: "Motion sensors:", required: false, multiple: true
        	paragraph "The room will be considered occupied while any of these lights are on."
            input "LightSwitches1", "capability.switch", title: "Light switches:", required: false, multiple: true
        }
        section("Heating")
        {
        	paragraph "Space heaters switches will turn on when the room is colder than the the rest of the house."
        	input "SpaceHeaters1", "capability.switch", title: "Space heater switch:", required: false, multiple: true
            paragraph "Heating fans will turn on when space heaters are on and stay on for a short while after to circulate air."
            input "HeatingFans1", "capability.switch", title: "Heating fans:", required: false, multiple: true
            paragraph "How long should heating fans stay on after the space heaters have turned off."
            input "HeatingFanDelay1", "number", title: "Fan delay (minutes):", required: false
        }
        section("Cooling")
        {
        	paragraph "Air conditioner switches will turn on when the room is warmer than the rest of the house."
        	input "AirConditioners1", "capability.switch", title: "Air conditioner switch:", required: false, multiple: true
            paragraph "Cooling fans will turn on when air conditioners are on and stay on for a short while after to circulate air."
            input "CoolingFans1", "capability.switch", title: "Cooling fans:", required: false, multiple: true
            paragraph "How long should cooling fans stay on after the air conditioners have turned off."
            input "CoolingFanDelay1", "number", title: "Fan delay (minutes):", required: false
        }
    }
}

def RoomPage2()
{
    dynamicPage(name: "RoomPage2", title: "Room 2", nextPage: GetNextPage(2), install: false, uninstall: false)
    {
        section("Room Info")
        {
        	paragraph "You must enter a temperature sensor to manage this room."
			input "RoomName2", "text", title: "Room Name:", required: false
            paragraph "The system needs to know if the room has a vent that connects to your thermostat's central air system."
            input "HasVent2", "enum", title: "Has Vents:", required: false, options: ["Yes", "No"]
        }
        section("Tempareature")
        {
        	paragraph "Temperature sensors will average to tell the app what the temperature of the room is."
        	input "TemperatureSensors2", "capability.temperatureMeasurement", title: "Temperature sensor:", required: false, multiple: true
        	paragraph "If you find that the reported temperature is not accurate, you can offset it."
            input "TemperatureOffset2", "number", title: "Temperature sensor offset (degrees):", required: false
            input "TemperatureOffsetDirection2", "enum", title: "Temperature offset direction:", required: false, options: ["Up", "Down"]
        }
    	section("Occupancy")
        {
            paragraph "In these modes, this room will be considered occupied even if no motion is detected and no lights are on."
			input "RoomModes2", "mode", title: "Occupied modes:", required: false, multiple: true
        	paragraph "The room will be considered occupied while there is motion."
        	input "MotionSensors2", "capability.motionSensor", title: "Motion sensors:", required: false, multiple: true
        	paragraph "The room will be considered occupied while any of these lights are on."
            input "LightSwitches2", "capability.switch", title: "Light switches:", required: false, multiple: true
        }
        section("Heating")
        {
        	paragraph "Space heaters switches will turn on when the room is colder than the the rest of the house."
        	input "SpaceHeaters2", "capability.switch", title: "Space heater switch:", required: false, multiple: true
            paragraph "Heating fans will turn on when space heaters are on and stay on for a short while after to circulate air."
            input "HeatingFans2", "capability.switch", title: "Heating fans:", required: false, multiple: true
            paragraph "How long should heating fans stay on after the space heaters have turned off."
            input "HeatingFanDelay2", "number", title: "Fan delay (minutes):", required: false
        }
        section("Cooling")
        {
        	paragraph "Air conditioner switches will turn on when the room is warmer than the rest of the house."
        	input "AirConditioners2", "capability.switch", title: "Air conditioner switch:", required: false, multiple: true
            paragraph "Cooling fans will turn on when air conditioners are on and stay on for a short while after to circulate air."
            input "CoolingFans2", "capability.switch", title: "Cooling fans:", required: false, multiple: true
            paragraph "How long should cooling fans stay on after the air conditioners have turned off."
            input "CoolingFanDelay2", "number", title: "Fan delay (minutes):", required: false
        }
    }
}

def RoomPage3()
{
    dynamicPage(name: "RoomPage3", title: "Room 3", nextPage: GetNextPage(3), install: false, uninstall: false)
    {
        section("Room Info")
        {
        	paragraph "You must enter a temperature sensor to manage this room."
			input "RoomName3", "text", title: "Room Name:", required: false
            paragraph "The system needs to know if the room has a vent that connects to your thermostat's central air system."
            input "HasVent3", "enum", title: "Has Vents:", required: false, options: ["Yes", "No"]
        }
        section("Tempareature")
        {
        	paragraph "Temperature sensors will average to tell the app what the temperature of the room is."
        	input "TemperatureSensors3", "capability.temperatureMeasurement", title: "Temperature sensor:", required: false, multiple: true
        	paragraph "If you find that the reported temperature is not accurate, you can offset it."
            input "TemperatureOffset3", "number", title: "Temperature sensor offset (degrees):", required: false
            input "TemperatureOffsetDirection3", "enum", title: "Temperature offset direction:", required: false, options: ["Up", "Down"]
        }
    	section("Occupancy")
        {
            paragraph "In these modes, this room will be considered occupied even if no motion is detected and no lights are on."
			input "RoomModes3", "mode", title: "Occupied modes:", required: false, multiple: true
        	paragraph "The room will be considered occupied while there is motion."
        	input "MotionSensors3", "capability.motionSensor", title: "Motion sensors:", required: false, multiple: true
        	paragraph "The room will be considered occupied while any of these lights are on."
            input "LightSwitches3", "capability.switch", title: "Light switches:", required: false, multiple: true
        }
        section("Heating")
        {
        	paragraph "Space heaters switches will turn on when the room is colder than the the rest of the house."
        	input "SpaceHeaters3", "capability.switch", title: "Space heater switch:", required: false, multiple: true
            paragraph "Heating fans will turn on when space heaters are on and stay on for a short while after to circulate air."
            input "HeatingFans3", "capability.switch", title: "Heating fans:", required: false, multiple: true
            paragraph "How long should heating fans stay on after the space heaters have turned off."
            input "HeatingFanDelay3", "number", title: "Fan delay (minutes):", required: false
        }
        section("Cooling")
        {
        	paragraph "Air conditioner switches will turn on when the room is warmer than the rest of the house."
        	input "AirConditioners3", "capability.switch", title: "Air conditioner switch:", required: false, multiple: true
            paragraph "Cooling fans will turn on when air conditioners are on and stay on for a short while after to circulate air."
            input "CoolingFans3", "capability.switch", title: "Cooling fans:", required: false, multiple: true
            paragraph "How long should cooling fans stay on after the air conditioners have turned off."
            input "CoolingFanDelay3", "number", title: "Fan delay (minutes):", required: false
        }
    }
}

def installed()
{
	initialize()
}

def updated()
{
	unsubscribe()
	initialize()
}

def GetTemperatureSensors(room_index)
{
	return settings["TemperatureSensors${room_index + 1}"]
}

def GetTemperatureOffset(room_index)
{
	def offset = settings["TemperatureOffset${room_index + 1}"]
    if(!offset)
    {
    	return 0
    }
    if(settings["TemperatureOffsetDirection${room_index + 1}"] == "Down")
    {
    	return offset * -1;
    }
    else
    {
		return offset
    }
	return offset
}

def GetRoomModes(room_index)
{
	return settings["RoomModes${room_index + 1}"]
}

def GetMotionSensors(room_index)
{
	return settings["MotionSensors${room_index + 1}"]
}

def GetLightSwitches(room_index)
{
	return settings["LightSwitches${room_index + 1}"]
}

def GetSpaceHeaters(room_index)
{
	return settings["SpaceHeaters${room_index + 1}"]
}

def GetHeatingFans(room_index)
{
	return settings["HeatingFans${room_index + 1}"]
}

def GetHeatingFanDelay(room_index)
{
	def ret = settings["HeatingFanDelay${room_index + 1}"]
    if(!ret)
    {
    	ret = "0"
    }
    return ret.toInteger()
}

def GetAirConditioners(room_index)
{
	return settings["AirConditioners${room_index + 1}"]
}

def GetCoolingFans(room_index)
{
	return settings["CoolingFans${room_index + 1}"]
}

def GetHasVent(room_index)
{
	def ret = settings["HasVent1${room_index + 1}"]
    return (!ret || ret == "Yes")
}

def GetCoolingFanDelay(room_index)
{
	def ret = settings["CoolingFanDelay${room_index + 1}"]
    if(!ret)
    {
    	ret = "0"
    }
    return ret.toInteger()
}

def GetTemperature(room_index)
{
	def temp_sesnsors = GetTemperatureSensors(room_index)
    if(temp_sesnsors)
    {
    	int len = temp_sesnsors.size()
    	int total_temp = 0
    	for(def i = 0; i < len; i++)
        {
        	total_temp = total_temp + temp_sesnsors[i].currentValue("temperature")
        }
        return (total_temp / len) + GetTemperatureOffset(room_index)
    }
    else if(Thermostat)
    {
    	return Thermostat.currentValue("temperature")
    }
    return ComfortTemperature
}



def GetOccupancy(room_index)
{
	int room_number = room_index + 1
    
    def occ_modes = GetRoomModes(room_index)
    if(occ_modes && occ_modes.contains(location.mode))
    {
    	return true
    }
    
    def motion_sensors = GetMotionSensors(room_index)
    if(motion_sensors)
    {
        for(def i = 0; i < motion_sensors.size(); i++)
        {
        	if(motion_sensors[i].currentValue("motion") == "active")
            {
            	return true
            }
        }
    }
    
    def light_switches = GetLightSwitches(room_index)
    if(light_switches)
    {
        for(def i = 0; i < light_switches.size(); i++)
        {
        	if(light_switches[i].currentValue("switch") == "on")
            {
            	return true
            }
        }
    }
    
    return false
}

def initialize()
{
	//initialize state with default values
    int iRoomCount = RoomCount.toInteger()
    state.RoomTemperature = []
    state.RoomOccupied = []
    state.RoomLeach = [] //with no managed heat/cool but with a temp sensor
    state.RoomStatus = []
    for(def i = 0; i < iRoomCount; i++)
    {
    	state.RoomTemperature << GetTemperature(i)
        state.RoomOccupied << GetOccupancy(i)
        state.RoomLeach << (GetTemperatureSensors(i) && !GetHasVent(i) && !GetSpaceHeaters(i) && !GetAirConditioners(i))
        state.RoomStatus << ""
    }
    UpdateOutsideTemperature()
    state.IsThermostatInRoom = false
    
    //subscribe to temperatures events
    if(Thermostat)
    {
		subscribe(Thermostat, "temperature", ThermostatTemperatureHandler)
    }
    for(def i = 0; i < iRoomCount; i++)
    {
    	def temp_sensors = GetTemperatureSensors(i)
        if(temp_sensors)
        {
			subscribe(temp_sensors, "temperature", TemperatureHandler)
            if(Thermostat)
            {
                int len = temp_sensors.size()
                for(def j = 0; j < len; j++)
                {
                	if(Thermostat.id == temp_sensors[j].id)
                    {
                    	state.IsThermostatInRoom = true
                    }
                }
            }
        }
    }
    UpdateHouseTemperature()
    
    //subscribe to motion events
    for(def i = 0; i < iRoomCount; i++)
    {
    	def motion_sensors = GetMotionSensors(i)
        if(motion_sensors)
        {
			subscribe(motion_sensors, "motion", MotionHandler)
        }
    }
    
    //subscribe to light switch events
    for(def i = 0; i < iRoomCount; i++)
    {
    	def light_switches = GetLightSwitches(i)
        if(light_switches)
        {
			subscribe(light_switches, "switch", SwitchHandler)
        }
    }
    
    //subscribe to application level events
	subscribe(location, "mode", ModeChangeHandler)
    runEvery1Hour(UpdateOutsideTemperatureHandler)
    state.sleeping = false
    if(SleepingStartTime && SleepingEndTime)
    {
		schedule(SleepingStartTime, SleepingStartTimeHandler)
		schedule(SleepingEndTime, SleepingEndTimeHandler)
	}
    UpdateClimate()
}

def SleepingStartTimeHandler()
{
	state.sleeping = true
	UpdateOutsideTemperature()
    UpdateClimate()
}
def SleepingEndTimeHandler()
{
	state.sleeping = false
	UpdateOutsideTemperature()
    UpdateClimate()
}

def UpdateOutsideTemperatureHandler()
{
	UpdateOutsideTemperature()
    UpdateClimate()
}

def UpdateRoomStatus(room_index, new_status)
{
	def old_status = state.RoomStatus[room_index]
	if(new_status != old_status)
    {
    
    	switch(old_status)
        {
        	case "heat":
            	//stop heaters
                def heaters = GetSpaceHeaters(room_index)
                def heatfans = GetHeatingFans(room_index)
                if(heaters)
                {
                	heaters.off()
                }
                if(heatfans)
                {
	                def heatfandelay = GetHeatingFanDelay(room_index)
                	if(!heatfandelay || heatfandelay == 0)
                    {
                    	heatfans.off()
                    }
                    else
                    {
                    	runIn(60 * heatfandelay, {heatfans.off()})
                    }
                }
            	break
            case "cool":
            	//stop air conditioners
                def acs = GetAirConditioners(room_index)
                def acfans = GetCoolingFans(room_index)
                if(acs)
                {
                	acs.off()
                }
                if(acfans)
                {
	                def acfandelay = GetCoolingFanDelay(room_index)
                	if(!acfandelay || acfandelay == 0)
                    {
                    	acfans.off()
                    }
                    else
                    {
                    	runIn(60 * acfandelay, {acfans.off()})
                    }
                }
            	break
        }
        
        switch(new_status)
        {
        	case "heat":
            	//start heaters
                def heaters = GetSpaceHeaters(room_index)
                def heatfans = GetHeatingFans(room_index)
                if(heaters)
                {
                	heaters.on()
                }
                if(heatfans)
                {
                	heatfans.on()
                }
            	break
            case "cool":
            	//start air conditioners
                def acs = GetAirConditioners(room_index)
                def acfans = GetCoolingFans(room_index)
                if(acs)
                {
                	acs.on()
                }
                if(acfans)
                {
                	acfans.on()
                }
            	break
        }
        
	    state.RoomStatus[room_index] = new_status
    }
}

def GetWeatherCurrent() {
    try
    {
        return getWeatherFeature("conditions")
    }
    catch (e)
    {
        log.error "error: $e"
        return null
    }
}


def UpdateOutsideTemperature()
{
	if(!state.OutsideTemperatureLastUpdatedTicks || now() > state.OutsideTemperatureLastUpdatedTicks + 3600000) //1 hour
    {
    	//get leach temp
    	def avg_leach = null
        int leach_count = 0
        int leach_total = 0
        int iRoomCount = RoomCount.toInteger()
        for(def i = 0; i < iRoomCount; i++)
        {
        	if(state.RoomLeach[i])
            {
            	leach_count = leach_count + 1
                leach_total = leach_total + GetTemperature(i)
            }
        }
        if(leach_count > 0)
        {
        	avg_leach = leach_total / leach_count
        }
        
        //log.debug "avg_leach=${avg_leach}"
        
    	def w = GetWeatherCurrent()
        //log.debug "w=${w}"
        if(w)
        {
        	//weather data
            def f = w.current_observation.temp_f
            //log.debug "f=${f}"
            state.OutsideTemperatureLastUpdatedTicks = now()

            if(avg_leach)
            {
            	//weather + leach data
	            state.OutsideTemperature = (f + avg_leach) / 2
            }
            else
            {
            	//weather only
	            state.OutsideTemperature = f
            }
		}
        else
        {
        	//no weather data
            if(avg_leach)
            {
            	//leach only data
                state.OutsideTemperature = avg_leach
            }
            else
            {
            	//no data at all
                state.OutsideTemperature = null
            }
        }
    }
}

def UpdateHouseTemperature()
{
	int iRoomCount = RoomCount.toInteger()
	int temp_count = 0
    int temp_sum = 0
    int total_count = 0
    int total_sum = 0
    if(Thermostat && !state.IsThermostatInRoom)
    {
    	temp_count = 1
        temp_sum = Thermostat.currentValue("temperature")
        total_count = 1
        total_sum = temp_sum
    }
    for(int i = 0; i < iRoomCount; i++)
    {
    	if(state.RoomTemperature[i])
        {
            if(state.RoomOccupied[i] && !state.RoomLeach[i])
            {
                temp_count = temp_count + 1
                temp_sum = temp_sum + state.RoomTemperature[i]
            }
            total_count = temp_count + 1
            total_sum = temp_sum + state.RoomTemperature[i]
        }
    }
    if(temp_count == 0)
    {
    	state.HouseTemperature = total_sum / total_count
    }
    else
    {
	    state.HouseTemperature = temp_sum / temp_count
    }
}

def ThermostatTemperatureHandler(evt)
{
	if(!state.IsThermostatInRoom) //only if thermostat is seperate from room logic
    {
		UpdateHouseTemperature()
        UpdateOutsideTemperature()
        UpdateClimate()
    }
}

def UpdateTemperatures()
{
    int iRoomCount = RoomCount.toInteger()
    for(def i = 0; i < iRoomCount; i++)
    {
    	state.RoomTemperature[i] = GetTemperature(i)
    }
}

def TemperatureHandler(evt)
{
	UpdateTemperatures()
    UpdateHouseTemperature()
    UpdateOutsideTemperature()
    UpdateClimate()
}

def UpdateOccupancy()
{
    int iRoomCount = RoomCount.toInteger()
    for(def i = 0; i < iRoomCount; i++)
    {
        state.RoomOccupied[i] = GetOccupancy(i)
    }
}

def MotionHandler(evt)
{
	UpdateOccupancy()
	UpdateOutsideTemperature()
    UpdateClimate()
}

def SwitchHandler(evt)
{
	UpdateOccupancy()
	UpdateOutsideTemperature()
    UpdateClimate()
}

def ModeChangeHandler(evt)
{
	UpdateOccupancy()
	UpdateOutsideTemperature()
    UpdateClimate()
}

def UpdateClimate()
{
	//decide high/low range based on weather
	def high_temp = ComfortTemperature
    def low_temp = ComfortTemperature
	if(state.OutsideTemperature)
    {
    	//weather data available
	    if(state.OutsideTemperature > ComfortTemperature + 2)
        {
        	//hot outside - avoid heating
            low_temp = low_temp - 10
        }
        else if(state.OutsideTemperature < ComfortTemperature - 2)
        {
        	//cold outside - avoid cooling
            high_temp = high_temp + 10
        }
        else
        {
        	//perfect outside - let the outside do the work instead of the system
            high_temp = high_temp + 10
            low_temp = low_temp - 10
        }
    }
    else
    {
    	//no weather data
        high_temp = high_temp + 2
        low_temp = low_temp - 2
    }
    
    //adjust high/low range for current mode
	if((SleepingModes && SleepingModes.contains(location.mode)) || state.sleeping)
    {
        high_temp = SleepingMaxTemperature
    	low_temp = SleepingMinTemperature
    }
    else if(AwayModes && AwayModes.contains(location.mode))
    {
    	high_temp = AwayMaxTemperature
    	low_temp = AwayMinTemperature
    }
    
    //perform climate update
    if(Thermostat && !state.IsThermostatInRoom && RoomCount == "0")
    {
    	//thermostat only case we can use set points to manage the central air
        switch(Thermostatmode)
        {
        	case "Heating and Cooling":
	        	Thermostat.auto()
            	break;
        	case "Heating Only":
	        	Thermostat.heat()
            	break;
        	case "Cooling Only":
	        	Thermostat.cool()
            	break;
        }
        Thermostat.setHeatingSetpoint(low_temp)
        Thermostat.setCoolingSetpoint(high_temp)
        Thermostat.poll()
    }
    else
    {
    	double temp_offset = Thermostat.currentValue("temperature") - state.HouseTemperature
        if(Thermostat && ThermostatMode == "Heating and Cooling")
        {
        	//use central air to heat/cool the house
            Thermostat.auto()
            Thermostat.setHeatingSetpoint(low_temp + temp_offset)
            Thermostat.setCoolingSetpoint(high_temp + temp_offset)
            Thermostat.poll()
            ManageIndependentRooms(low_temp, high_temp)
        }
        else if(Thermostat && ThermostatMode == "Heating Only" && state.HouseTemperature <= high_temp + temp_offset)
        {
            Thermostat.heat()
            Thermostat.setHeatingSetpoint(low_temp + temp_offset)
            Thermostat.setCoolingSetpoint(high_temp + temp_offset)
            Thermostat.poll()
            ManageIndependentRooms(low_temp, high_temp)
        }
        else if(Thermostat && ThermostatMode == "Cooling Only" && state.HouseTemperature >= low_temp + temp_offset)
        {
            Thermostat.cool()
            Thermostat.setHeatingSetpoint(low_temp + temp_offset)
            Thermostat.setCoolingSetpoint(high_temp + temp_offset)
            Thermostat.poll()
            ManageIndependentRooms(low_temp, high_temp)
        }
        else
        {
            //central air off, not applicable or non-existant - manage rooms independently
            if(Thermostat)
            {
	            Thermostat.off()
            }
            
		    int iRoomCount = RoomCount.toInteger()
            for(def i = 0; i < iRoomCount; i++)
            {
            	ManageRoom(i, low_temp, high_temp)
            }
        }
    }
}

def ManageIndependentRooms(low_temp, high_temp)
{
    int iRoomCount = RoomCount.toInteger()
	for(def i = 0; i < iRoomCount; i++)
    {
    	if(GetHasVent(i))
        {
        	//rooms with vents are being handled by the central air--- leave off
	    	UpdateRoomStatus(i, "")
        }
        else
        {
        	//room without vents -- keep headers/ac on if needed
        	ManageRoom(i, low_temp, high_temp)
        }
    }
}

def ManageRoom(room_index, low_temp, high_temp)
{
    if(state.RoomOccupied[room_index])
    {
        if(state.RoomTemperature[room_index] < low_temp)
        {
            //the room is cold - heat it up
            UpdateRoomStatus(room_index, "heat")
        }
        else if(state.RoomTemperature[room_index] > high_temp)
        {
            //the room is hot - cool it down
            UpdateRoomStatus(room_index, "cool")
        }
        else
        {
            //the room is just right - leave it alone
            UpdateRoomStatus(room_index, "")
        }
    }
    else
    {
        //do not manage temperature for unoccupied rooms
        UpdateRoomStatus(room_index, "")
    }
}
