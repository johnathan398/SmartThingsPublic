/**
 *  Smart Light
 *
 *  Controls a set of lights with intelligence.
 *   -Detects presence based on motion, contact sensor (open for closets or closed for bathrooms),
      humidity (if you are in the shower) or another switch
 *   -Define "presence" according to a mode or schedule
 *   -Presence criteria are or-ed together
 *   -Presence Light mode turns on the light anytime presence is detected
 *   -Presence Night Light mode turns on the light when presence is detected and it's dark
 *   -Night Light mode turns on the light when it's dark and off when it's light
 *   -Allows for a dim setting during certain modes (late-nightlights)
 *   -Allows for auto-shutoff some time after presence is no longer detected
 *   -Allows for special rules when switch is manually touched
 *
 *  Copyright 2015 John Fullman
 *  GNU General Public License v2 (https://www.gnu.org/licenses/gpl-2.0.txt)
 *
 */
definition(
    name: "Smart Light",
    namespace: "john.fullman",
    author: "john.fullman@gmail.com",
    description: "Turns your lights on and off based on presence and environment.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet-luminance.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet-luminance@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet-luminance@2x.png"
)

preferences
{
	section("Lights to Control")
    {
		input "Lights", "capability.switch", title: "On/Off Lights:", required: false, multiple: true
		input "DimmableLights", "capability.switchLevel", title: "Dimmable Lights:", required: false, multiple: true
	}
	section("Presence Detection")
    {
		input "PresenceMotion", "capability.motionSensor", title: "Presence when motion:", required: false, multiple: true
    	input "PresenceContactOpen", "capability.contactSensor", title: "Presence when open:", required: false, multiple: true
    	input "PresenceContactClosed", "capability.contactSensor", title: "Presence when closed:", required: false, multiple: true
        input "PresenceSwitch", "capability.switch", title: "Presence when any switch on:", required: false, multiple: true
		input "PresenceHumidity", "capability.relativeHumidityMeasurement", title: "Presence when humid:", required: false
        input "HumidityRateThreshold", "number", title: "Consider present when humidity climbing at a rate of (%/min):", required: false, defaultValue: 2
        input "HumidityThreshold", "number", title: "Or when humidity is above (%):", required: false, defaultValue: 70
        paragraph "When humidity begins to drop and below threshold, the humidity sensor will report as not present."
	}
    section("Presence Schedule")
    {
    	input "PresenceModes", "mode", title: "Consider present when in these modes:", required: false, multiple: true
        input "PresenceStart", "time", title: "Consider present starting from:", required: false
        input "PresenceEnd", "time", title: "until:", required: false
    }
    section("Control Rules")
    {
    	input "ControlMode", "enum", title: "Light control type:", options: ["Presence Light", "Presence Night Light", "Night Light"], required: true, defaultValue: "Presence Light"
		input "OffDelayMinutes", "number", title: "How long after presence is no longer detected to turn off (minutes):", required: true, defaultValue: 1
    	input "DimModes", "mode", title: "Which modes should the dimmable lights come on dimly:", required: false, multiple: true
    	input "DimLevel", "number", title: "What level is considered dim (0-100)?", required: false, defaultValue: 10
    }
    section("Manual Activation")
    {
    	input "ManualMode", "enum", title: "What should the lights do when they are manually activated?", options: ["Stay On", "Same As Presence", "Turn Off After Timeout"], required: true, defaultValue: "Same As Presence"
        input "ManualTimeoutMinutes", "number", title: "How long after manual on should the lights turn off (minutes)?", required: false
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

def initialize()
{
	state.presence = IsPresent(null)
    state.sunup = true
    state.AutomaticallyTurnedOn = false
    state.humidpresence = false
    if(Lights)
    {
    	subscribe(Lights, "switch", LightsHandler)
    }
    if(DimmableLights)
    {
    	subscribe(DimmableLights, "switch", LightsHandler)
    }
    if(PresenceMotion)
    {
		subscribe(PresenceMotion, "motion", MotionHandler)
    }
    if(PresenceContactOpen)
    {
		subscribe(PresenceContactOpen, "contact", ContactHandler)
    }
    if(PresenceContactClosed)
    {
		subscribe(PresenceContactClosed, "contact", ContactHandler)
    }
    if(PresenceSwitch)
    {
    	subscribe(PresenceSwitch, "switch", PresenceSwitchHandler)
    }
    if(PresenceHumidity)
    {
	    subscribe(PresenceHumidity, "humidity", HumidityHandler)
    }
    subscribe(location, "sunriseTime", SunriseTimeHandler)
    subscribe(location, "sunsetTime", SunsetTimeHandler)
    if(PresenceModes)
    {
    	subscribe(location, "mode", ModeChangeHandler)
    }
    if(PresenceStart && PresenceEnd)
    {
    	schedule(PresenceStart, PresenceStartHandler)
    	schedule(PresenceEnd, PresenceEndHandler)
        state.timepresence = false
	}
}

def IsPresent(evt)
{
    if(PresenceMotion)
    {
    	for(int i = 0; i < PresenceMotion.size(); i++)
        {
        	def statei = PresenceMotion[i].currentValue("motion")
            if(evt && PresenceMotion[i].id == evt.deviceId && (evt.value == "active" || evt.value == "inactive"))
            {
            	statei = evt.value
            }
        	if(statei == "active")
           	{
            	return true
            }
        }
    }
    if(PresenceContactOpen)
    {
    	for(int i = 0; i < PresenceContactOpen.size(); i++)
        {
        	def statei = PresenceContactOpen[i].currentValue("contact")
            if(evt && PresenceContactOpen[i].id == evt.deviceId && (evt.value == "open" || evt.value == "closed"))
            {
            	statei = evt.value
            }
        	if(statei == "open")
           	{
            	return true
            }
        }
    }
    if(PresenceContactClosed)
    {
    	for(int i = 0; i < PresenceContactClosed.size(); i++)
        {
        	def statei = PresenceContactClosed[i].currentValue("contact")
            if(evt && PresenceContactClosed[i].id == evt.deviceId && (evt.value == "open" || evt.value == "closed"))
            {
            	statei = evt.value
            }
        	if(statei == "closed")
           	{
            	return true
            }
        }
    }
    if(PresenceSwitch)
    {
    	for(int i = 0; i < PresenceSwitch.size(); i++)
        {
        	def statei = PresenceSwitch[i].currentValue("switch")
            if(evt && PresenceSwitch[i].id == evt.deviceId)
            {
            	statei = evt.value
            }
            if(statei == "on")
            {
            	return true
            }
        }
    }
    if(PresenceHumidity)
    {
    	if(state.humidpresence)
        {
        	return true
        }
    }
    
    if(PresenceModes && PresenceModes.contains(location.mode))
    {
    	return true
	}
    if(PresenceStart && PresenceEnd && state.timepresence)
    {
    	return true
    }
    
    return false
}

def PresenceUpdate(evt)
{
	def currentlypresent = IsPresent(evt)
	//log.debug "PresenceUpdate(): ${state.presence} => ${currentlypresent} auto: ${state.AutomaticallyTurnedOn}"
	if(!state.presence && currentlypresent && (ControlMode == "Presence Light" || (ControlMode == "Presence Night Light" && !state.sunup)))
    {
	    state.AutomaticallyTurnedOn = true
        state.AutomaticallyTurnedOnAt = now()
    	LightsOn()
    }
    else if(state.presence && !currentlypresent && (ControlMode == "Presence Light" || ControlMode == "Presence Night Light") && (state.AutomaticallyTurnedOn || ManualMode == "Same As Presence"))
    {
    	if(!OffDelayMinutes || OffDelayMinutes == 0)
        {
	    	LightsOff()
        }
        else
        {
        	runIn(60 * OffDelayMinutes, ScheduledLightsOffNoPresence)
        }
    }
    state.presence = currentlypresent
}

def MotionHandler(evt)
{
	PresenceUpdate(evt)
}
def ContactHandler(evt)
{
	PresenceUpdate(evt)
}

def HumidityHandler(evt)
{
	state.humidpresence = IsHumidityPresent(evt, HumidityRateThreshold, HumidityThreshold, state.humidpresence)
	PresenceUpdate(evt)
}

def IsHumidityPresent(evt, incrate, threshold, currpres)
{
    def states = evt.device.eventsSince(new Date((long)(evt.date.getTime() - (2.1 * 60000))), [all:true, max: 10]).findAll{it.name == "humidity"}
    
    double lastevtvalue = Double.parseDouble(evt.value.replace("%", ""))
    def lastevtdate = evt.date
    if(threshold && lastevtvalue >= threshold)
    {
    	return true
    }
    
    boolean anyposrate = false
    if (states)
    {
    	for(int i = 0; i < states.size(); i++)
        {
        	if(states[i].date.before(lastevtdate))
            {
                double nextevtvalue = Double.parseDouble(states[i].value.replace("%", ""))
                def nextevtdate = states[i].date
                double rate = (lastevtvalue - nextevtvalue) / ((lastevtdate.getTime() - nextevtdate.getTime()) / 60000)
                if(rate >= incrate)
                {
                	return true
                }
                if(rate > 0)
                {
                	anyposrate = true
                }
                lastevtvalue = nextevtvalue
                lastevtdate = nextevtdate
            }
        }
    }

	return currpres && anyposrate
}

def PresenceSwitchHandler(evt)
{
	PresenceUpdate(evt)
}

def SunriseTimeHandler(evt)
{
	state.sunup = true
    state.presence = IsPresent(null)
    if(ControlMode == "Night Light" || ControlMode == "Presence Night Light")
    {
    	LightsOff()
    }
}

def SunsetTimeHandler(evt)
{
	state.sunup = false
    state.presence = IsPresent(null)
    if(ControlMode == "Night Light" || (ControlMode == "Presence Night Light" && state.presence))
    {
	    state.AutomaticallyTurnedOn = true
        state.AutomaticallyTurnedOnAt = now()
    	LightsOn()
    }
}

def LightsOn()
{
	if(Lights)
    {
    	Lights.on()
    }
	if(DimmableLights)
    {
    	//set level for any dimmable lights currently off - leave lights with level already set alone
    	for(int i = 0; i < DimmableLights.size(); i++)
        {
            def sw = DimmableLights[i].currentValue("switch");
        	if(!sw || sw == "off")
            {
                if(DimModes && DimModes.contains(location.mode))
                {
                    DimmableLights.setLevel(DimLevel)
                }
                else
                {
                    DimmableLights.setLevel(100)
                }
            }
        }
    }
}

def LightsOff()
{
	if(Lights)
    {
    	Lights.off()
    }
	if(DimmableLights)
    {
        DimmableLights.off()
    }
}

def ScheduledLightsOffNoPresence()
{
	if(!state.presence && (state.AutomaticallyTurnedOn || ManualMode == "Same As Presence"))
    {
    	LightsOff()
    }
}

def ScheduledLightsOffManual()
{
	if(!state.AutomaticallyTurnedOn)
    {
    	LightsOff()
    }
}

def LightsHandler(evt)
{
	if(evt.isPhysical())
    {
    	//we've touched a light that was auto activated... turn off auto control
    	state.AutomaticallyTurnedOn = false
        
        if((evt.value != "off") && ManualMode == "Turn Off After Timeout" && ManualTimeoutMinutes)
        {
        	runIn(60 * ManualTimeoutMinutes, ScheduledLightsOffManual)
        }
    }
}

def ModeChangeHandler(evt)
{
    PresenceUpdate(null)
}

def PresenceStartHandler()
{
	state.timepresence = true
    PresenceUpdate(null)
}

def PresenceEndHandler()
{
	state.timepresence = false
    PresenceUpdate(null)
}