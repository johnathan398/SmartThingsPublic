/**
 *  Smart Sequence
 *
 *  Performs a sequence of actions.
 *    -Supported Triggers: switches (tap, double tap), schedule times
 *    -Supported Actions: switches
 *    -Supports up to 3 actions
 *
 *  Copyright 2015 John Fullman
 *  GNU General Public License v2 (https://www.gnu.org/licenses/gpl-2.0.txt)
 *
 */

definition(
    name: "Smart Sequence",
    namespace: "john.fullman",
    author: "John Fullman",
    description: "Performs a sequence of actions.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/MiscHacking/remote.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/MiscHacking/remote@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/MiscHacking/remote@2x.png")


preferences
{
	section("Trigger")
    {
    	input "TriggerAction", "enum", options: ["Anything", "Turn Off", "Turn On", "Double Tap", "Double Tap On", "Double Tap Off"], title: "Activate sequence when I do this:", required: true, defaultValue: "Anything"
    	input "TriggerSwitches", "capability.switch", title: "On any of these switches:", required: false, multiple: true
        input "TriggerTimes", "time", title: "At these times:", required: false, multiple: true
    }
	section("Action 1")
    {
		input "Delay1", "number", title: "Delay (in seconds):", required: true, defaultValue: 0
        input "ActionType1", "enum", options: ["No Action", "Turn On", "Turn Off"], title: "Do this:", required: true, defaultValue: "No Action"
        input "Switches1", "capability.switch", title: "With these switches:", required: false, multiple: true
	}
	section("Action 2")
    {
		input "Delay2", "number", title: "Delay (in seconds):", required: true, defaultValue: 0
        input "ActionType2", "enum", options: ["No Action", "Turn On", "Turn Off"], title: "Do this:", required: true, defaultValue: "No Action"
        input "Switches2", "capability.switch", title: "With these switches:", required: false, multiple: true
	}
	section("Action 3")
    {
		input "Delay3", "number", title: "Delay (in seconds):", required: true, defaultValue: 0
        input "ActionType3", "enum", options: ["No Action", "Turn On", "Turn Off"], title: "Do this:", required: true, defaultValue: "No Action"
        input "Switches3", "capability.switch", title: "With these switches:", required: false, multiple: true
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
	if(TriggerSwitches)
    {
		subscribe(TriggerSwitches, "switch", TriggerSwitchHandler, [filterEvents: false])
    }
    if(TriggerTimes)
    {
    	for(def i = 0; i < TriggerTimes.size(); i++)
        {
	    	schedule(TriggerTimes[i], TriggerHanlder)
        }
    }
}

def TriggerHanlder()
{
	ScheduleAction(1)
}

def TriggerSwitchHandler(evt)
{
	if(TriggerAction == "Anything"
      || (TriggerAction == "Turn Off" && evt.value == "off")
      || (TriggerAction == "Turn On" && evt.value == "on")
      || ((TriggerAction == "Double Tap" || TriggerAction == "Double Tap On") && IsDoubleTap(evt.device, evt, "on"))
      || ((TriggerAction == "Double Tap" || TriggerAction == "Double Tap Off") && IsDoubleTap(evt.device, evt, "off"))
      )
    {
    	ScheduleAction(1)
    }
}

def ScheduleAction(i)
{
	def Delay = settings["Delay${i}"]
    if(Delay == 0)
    {
    	RunAction(i)
    }
    else
    {
    	switch(i)
        {
        	case 1:
				runIn(Delay, RunAction1)
            	break;
        	case 2:
				runIn(Delay, RunAction2)
            	break;
        	case 3:
				runIn(Delay, RunAction3)
            	break;
        }
    }
}

def RunAction1() { RunAction(1) }
def RunAction2() { RunAction(2) }
def RunAction3() { RunAction(3) }

def RunAction(i)
{
	def ActionType = settings["ActionType${i}"]
    def Switches = settings["Switches${i}"]
    
    switch(ActionType)
    {
    	case "Turn On":
        	Switches.on()
        	break;
    	case "Turn Off":
        	Switches.off()
        	break;
	}
    if(i <= 2)
    {
    	ScheduleAction(i+1)
    }
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
