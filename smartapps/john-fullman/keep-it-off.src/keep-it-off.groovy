/**
 *  Keep It Off
 *
 *  If a switch is turned on... then it's turned back off.
 *    -Turns off after a timeout
 *    -Will stay on if turned back on quickly (after timeout)
 *    -A double on-tap will prevent the switch from turning off 
 *
 *  Copyright 2015 John Fullman
 *  GNU General Public License v2 (https://www.gnu.org/licenses/gpl-2.0.txt)
 *
 */

definition(
    name: "Keep It Off",
    namespace: "john.fullman",
    author: "john.fullman@gmail.com",
    description: "If a switch is turned on, it will automatically be turned off after a set period of time.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-YourLightsAreOff.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-YourLightsAreOff@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-YourLightsAreOff@2x.png")


preferences
{
    section("Switch")
    {
        input "OffSwitch", "capability.switch", title: "Switch to keep off:", required: true
    }
    section("Behavior")
    {
		input "MinutesToLeaveOn", "number", title: "How long before turning off (minutes):", required: true
        input "GraceSeconds", "number", title: "If turned back on within x seconds, disable auto-shutoff:", required: true, defaultValue: 10
        input "OffMode", "enum", title: "Turn off when turned on via:", required: true, options: ["Physical Switch", "App", "Any"], defaultValue: "Any"
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
    subscribe(OffSwitch, "switch.on", SwitchHandler, [filterEvents: false])
}

def SwitchHandler(evt)
{
	def inmode = OffMode == "Any" || (OffMode == "Physical Switch" && evt.isPhysical()) || (OffMode == "App" && !evt.isPhysical())
    def isdoubletap = IsDoubleTap(evt.device, evt, "on")
	if(!isdoubletap && inmode && (!atomicState.lastoff || ((evt.date.getTime() - atomicState.lastoff) > GraceSeconds * 1000)))
    {
        if(MinutesToLeaveOn == 0)
        {
            TurnOffHandler()
        }
        else
        {
            runIn(60 * MinutesToLeaveOn, TurnOffHandler)
        }
    }
    if(isdoubletap)
    {
    	unschedule("TurnOffHandler")
    }
}

def TurnOffHandler()
{
	OffSwitch.off()
    atomicState.lastoff = (new Date()).getTime()
}

def IsDoubleTap(check_switch, evt, value)
{
	if(evt.value != value)
    {
    	return false
    }
    def states = check_switch.eventsSince(new Date(evt.date.getTime() - 3000), [all:true, max: 10]).findAll{it.name == "switch" && (it.isPhysical() || !it.type)}
    if (states)
    {
    	for(int i = 0; i < states.size(); i++)
        {
        	if(states[i].date.before(evt.date))
            {
            	return states[i].value == value
            }
        }
    }
    return false
}
