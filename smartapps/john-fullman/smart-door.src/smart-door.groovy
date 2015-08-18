/**
 *  Smart Door
 *
 *  Controls a doors with intelligence.
 *   -Unlock door when you arrive
 *   -Lock door when you leave
 *   -Notify when a door is left open when you leave
 *   -Notify when a lock is left unlocked when you leave
 *   -Automatically lock door when you leave
 *   -Automatically unlock door when you arrive
 *   -Get security alerts when there is door activity while you are away
 *
 *  Copyright 2015 John Fullman
 *  GNU General Public License v2 (https://www.gnu.org/licenses/gpl-2.0.txt)
 *
 */
definition(
    name: "Smart Door",
    namespace: "john.fullman",
    author: "john.fullman@gmail.com",
    description: "Controls a set of doors with intelligence.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Allstate/lock_it_when_i_leave.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Allstate/lock_it_when_i_leave@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Allstate/lock_it_when_i_leave@2x.png")


preferences
{
    section("Door Devices")
    {
    	input "DoorName", "text", title: "Door Name:", required: true
    	input "DoorControl", "capability.doorControl", title: "Door actuator:", required: false
    	input "DoorContact", "capability.contactSensor", title: "Door contact sensor:", required: false
    	input "DoorLock", "capability.lock", title: "Door Lock:", required: false
    }
    section("Mode Actions")
    {
		input "Modes1", "mode", title: "For these modes:", required: false, multiple: true
    	input "EnterModeAction1", "enum", title: "Do this at mode entry:", required: true, options: ["Nothing", "Lock", "Unlock", "Open", "Close", "Notify If Unlocked", "Notify If Open"], defaultValue: "Nothing"
    	input "ExitModeAction1", "enum", title: "Do this at mode exit:", required: true, options: ["Nothing", "Lock", "Unlock", "Open", "Close", "Notify If Unlocked", "Notify If Open"], defaultValue: "Nothing"
        input "ModeActivityNotice1", "boolean", title: "Notify if activity while in these modes:", required: true, defaultValue: false
    }
    section("Scheduled Actions")
    {
    	paragraph "Action 1"
    	input "ScheduleAction1", "enum", title: "Do this:", required: true, options: ["Nothing", "Lock", "Unlock", "Open", "Close", "Notify If Unlocked", "Notify If Open"], defaultValue: "Nothing"
    	input "ScheduleTime1", "time", title: "At this time:", required: false
    	paragraph "Action 2"
    	input "ScheduleAction2", "enum", title: "Do this:", required: true, options: ["Nothing", "Lock", "Unlock", "Open", "Close", "Notify If Unlocked", "Notify If Open"], defaultValue: "Nothing"
    	input "ScheduleTime2", "time", title: "At this time:", required: false
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
	subscribe(location, "mode", ModeChangeHandler)
    if(DoorControl)
    {
    	subscribe(DoorControl, "door", OpeningChangeHandler)
    }
    if(DoorContact)
    {
    	subscribe(DoorContact, "contact", OpeningChangeHandler)
    }
    if(DoorLock)
    {
    	subscribe(DoorLock, "lock", OpeningChangeHandler)
    }
    state.inmode = IsInMode()
    if(ScheduleTime1)
    {
    	schedule(ScheduleTime1, Time1Handler)
    }
    if(ScheduleTime2)
    {
    	schedule(ScheduleTime2, Time2Handler)
    }
}

def IsInMode()
{
	return Modes1 && Modes1.contains(location.mode)
}

def OpeningChangeHandler(evt)
{
	if(IsInMode() && ModeActivityNotice1 && evt.isPhysical())
    {
    	sendPush("${evt.device.displayName} is ${evt.value}")
    }
}

def ModeChangeHandler(evt)
{
	def currinmode = IsInmode()
    if(!state.inmode && currinmode)
    {
    	//mode entry
        DoAction(EnterModeAction1)
    }
    else if(state.inmode && !currinmode)
    {
    	//mode exit
        DoAction(ExitModeAction1)
    }
	state.inmode = currinmode
}

def Time1Handler()
{
	DoAction(ScheduleAction1)
}

def Time2Handler()
{
	DoAction(ScheduleAction2)
}

def DoAction(act)
{
	switch(act)
    {
    	case "Lock":
        	if(DoorLock)
            {
            	if(!DoorContact || DoorContact.currentValue("contact") == "closed")
                {
                	DoorLock.lock()
                }
                else
                {
                	sendPush("${DoorLock.displayName} cannot lock. The door is open.")
                }
            }
            else
            {
            	sendPush("Cannot lock door. No door lock.")
            }
        	break
    	case "Unlock":
        	if(DoorLock)
            {
            	DoorLock.unlock()
            }
            else
            {
            	sendPush("Cannot unlock door. No door lock.")
            }
        	break
    	case "Open":
        	if(DoorControl)
            {
            	DoorControl.open()
            }
            else
            {
            	sendPush("Cannot open door. No door actuator.")
            }
        	break
    	case "Close":
        	if(DoorControl)
            {
            	DoorControl.close()
            }
            else
            {
            	sendPush("Cannot close door. No door actuator.")
            }
        	break
    	case "Notify If Unlocked":
        	if(DoorLock && DoorLock.currentValue("lock") == "unlocked")
            {
            	sendPush("${DoorLock.displayName} is unlocked.")
            }
        	break
        case "Notify If Open":
        	if(DoorLock && DoorLock.currentValue("contact") == "open")
            {
            	sendPush("${DoorLock.displayName} is open.")
            }
        	break
    }
}