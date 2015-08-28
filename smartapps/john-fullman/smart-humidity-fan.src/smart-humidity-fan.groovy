/**
 *  Smart Humidity Fan
 *
 *  Turns on a fan when you start taking a shower... turns it back off when you are done.
 *    -Uses humidity change rate for rapid response
 *    -Timeout option when manaully controled (for stench mitigation)
 *
 *  Copyright 2015 John Fullman
 *  GNU General Public License v2 (https://www.gnu.org/licenses/gpl-2.0.txt)
 *
 */

definition(
    name: "Smart Humidity Fan",
    namespace: "john.fullman",
    author: "john.fullman@gmail.com",
    description: "Control a fan (switch) based on relative humidity.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/water_moisture.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/water_moisture@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Meta/water_moisture@2x.png"
)

preferences
{
	section("Bathroom Devices")
    {
    	paragraph "NOTE: The humidity sensor you select will need to report about once per minute."
		input "HumiditySensor", "capability.relativeHumidityMeasurement", title: "Humidity Sensor:", required: true
		input "FanSwitch", "capability.switch", title: "Fan Location:", required: true
    }
    section("Fan Activation")
    {
		input "HumidityIncreaseRate", "number", title: "Humidity Increase Rate (%/min):", required: true, defaultValue: 2
        input "HumidityThreshold", "number", title: "Humidity Threshold (%):", required: false, defaultValue: 65
	}
    section("Fan Deactivation")
    {
		input "HumidityDropTimeout", "number", title: "Turn off how long after humidity begins to drop (minutes):", required: true, defaultValue:  10
	}
    section("Manual Activation")
    {
    	paragraph "When should the fan turn off when turned on manually?"
        input "ManualControlMode", "enum", title: "Off After Manual-On?", required: true, options: ["Manually", "By Humidity", "After Set Time"], defaultValue: "After Set Time"
        paragraph "How many minutes until the fan is auto-turned-off?"
        input "ManualOffMinutes", "number", title: "Auto Turn Off Time (minutes)?", required: false, defaultValue: 20
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
	state.humpres = false
    subscribe(HumiditySensor, "humidity", HumidityHandler)
    subscribe(FanSwitch, "switch", FanSwitchHandler)
}

def HumidityHandler(evt)
{
	state.humpres = IsHumidityPresent(evt, HumidityIncreaseRate, HumidityThreshold, state.humpres)

	//if the humidity is high (or rising fast) and the fan is off, kick on the fan
    if (state.humpres && FanSwitch.currentValue("switch") == "off")
    {
		state.AutomaticallyTurnedOn = true
        state.AutomaticallyTurnedOnAt = now()
       	FanSwitch.on()
    }
    //turn off the fan when humidity returns to normal and it was kicked on by the humidity sensor
    else if(!state.humpres && (state.AutomaticallyTurnedOn || ManualControlMode == "By Humidity"))
    {
        if(FanSwitch.currentValue("switch") == "on")
        {
            if(HumidityDropTimeout == 0)
            {
                FanSwitch.off()
            }
            else
            {
                runIn(60 * HumidityDropTimeout, TurnOffFanSwitchCheckHumidity)
            }
        }
        state.AutomaticallyTurnedOn = false
	}
}

def FanSwitchHandler(evt)
{
	switch(evt.value)
    {
    	case "on":
            if(evt.isPhysical())
            {
                if(!state.AutomaticallyTurnedOn && ManualControlMode == "After Set Time" && ManualOffMinutes)
                {
                    if(ManualOffMinutes == 0)
                    {
                        FanSwitch.off()
                    }
                    else
                    {
                        runIn(60 * ManualOffMinutes, TurnOffFanSwitch)
                    }
                }
			    state.AutomaticallyTurnedOn = false
            }
	        break
        case "off":
		    state.AutomaticallyTurnedOn = false
        	break
    }
}

def TurnOffFanSwitchCheckHumidity()
{
	if(FanSwitch.currentValue("switch") == "on")
    {
        if(state.HumidityChangeRate > 0)
        {
        	log.debug "Didn't turn off fan because humidity rate is ${state.HumidityChangeRate}"
            state.AutomaticallyTurnedOn = true
            state.AutomaticallyTurnedOnAt = now()
        }
        else
        {
        	FanSwitch.off()
        }
    }
}

def TurnOffFanSwitch()
{
    if(FanSwitch.currentValue("switch") == "on")
    {
        FanSwitch.off()
    }
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
