/**
 *  Keep 'Em Synced
 *
 *  Keeps a set of switches on the same on/off status.
 *    -Setup an un-used switch as a virtual 3-way
 *    -Treat all lights in a room as if they are on the same set of switches
 *
 *  Copyright 2015 John Fullman
 *  GNU General Public License v2 (https://www.gnu.org/licenses/gpl-2.0.txt)
 *
 */

definition(
    name: "Keep 'Em Synced",
    namespace: "john.fullman",
    author: "john.fullman@gmail.com",
    description: "Keeps a set of switches on the same on/off status.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance@2x.png")


preferences
{
    section("Switch")
    {
        input "SyncedSwitches", "capability.switch", title: "Switches to keep synced:", required: true, multiple: true
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
    subscribe(SyncedSwitches, "switch", SwitchHandler, [filterEvents: false])
}

def SwitchHandler(evt)
{
	//log.debug "evt.source=${evt.source};evt.deviceId=${evt.deviceId}; evt.isPhysical()=${evt.isPhysical()}; evt.installedSmartApp=${evt.installedSmartApp}; app.id=${app.id}"
    if(evt.isPhysical())
    {
    	switch(evt.value)
    	{
        	case "on":
    	        SyncedSwitches.on()
	            break
	        case "off":
        	    SyncedSwitches.off()
    	    	break
	    }
    }
}
