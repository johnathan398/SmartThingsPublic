/**
 *  Smart Modes
 *
 *  Controls your house's modes with dynamic rules.
 *   -Set a mode at a specific time
 *   -Certain modes only apply while away or home
 *   -Will only be set from certain prior modes (like a flowchart)
 *
 *  Copyright 2015 John Fullman
 *  GNU General Public License v2 (https://www.gnu.org/licenses/gpl-2.0.txt)
 *
 */
definition(
    name: "Smart Modes",
    namespace: "john.fullman",
    author: "john.fullman@gmail.com",
    description: "Controls your house's modes with dynamic rules.",
    category: "Convenience",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-BigButtonsAndSwitches.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-BigButtonsAndSwitches@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/App-BigButtonsAndSwitches@2x.png")


preferences
{
	section("Presence")
    {
		input "PresenceSensors", "capability.presenceSensor", title: "Sensors used for presence detection:", required: false, multiple: true
	}
    section("Mode 1")
    {
    	input "Mode0", "mode", title: "These rules apply to this mode:", required: false
        input "FilterAfterTime0", "time", title: "Set if after:", required: false
        input "FilterBeforeTime0", "time", title: "Set if before:", required: false
        input "FilterPresence0", "enum", title: "Set only when:", required: true, options: ["All Present", "All or Some Present", "Some Present", "Some or None Present", "Not Present", "Any"], defaultValue: "Any"
        input "FilterSun0", "enum", title: "Set only when:", required: true, options: ["Sun Is Up", "Sun Is Down", "Any"], defaultValue: "Any"
    	input "FilterFromMode0", "mode", title: "Set only from these modes:", required: false, multiple: true
    }
    section("Mode 2")
    {
    	input "Mode1", "mode", title: "These rules apply to this mode:", required: false
        input "FilterAfterTime1", "time", title: "Set if after:", required: false
        input "FilterBeforeTime1", "time", title: "Set if before:", required: false
        input "FilterPresence1", "enum", title: "Set only when:", required: true, options: ["All Present", "All or Some Present", "Some Present", "Some or None Present", "Not Present", "Any"], defaultValue: "Any"
        input "FilterSun1", "enum", title: "Set only when:", required: true, options: ["Sun Is Up", "Sun Is Down", "Any"], defaultValue: "Any"
    	input "FilterFromMode1", "mode", title: "Set only from these modes:", required: false, multiple: true
    }
    section("Mode 3")
    {
    	input "Mode2", "mode", title: "These rules apply to this mode:", required: false
        input "FilterAfterTime2", "time", title: "Set if after:", required: false
        input "FilterBeforeTime2", "time", title: "Set if before:", required: false
        input "FilterPresence2", "enum", title: "Set only when:", required: true, options: ["All Present", "All or Some Present", "Some Present", "Some or None Present", "Not Present", "Any"], defaultValue: "Any"
        input "FilterSun2", "enum", title: "Set only when:", required: true, options: ["Sun Is Up", "Sun Is Down", "Any"], defaultValue: "Any"
    	input "FilterFromMode2", "mode", title: "Set only from these modes:", required: false, multiple: true
    }
    section("Mode 4")
    {
    	input "Mode3", "mode", title: "These rules apply to this mode:", required: false
        input "FilterAfterTime3", "time", title: "Set if after:", required: false
        input "FilterBeforeTime3", "time", title: "Set if before:", required: false
        input "FilterPresence3", "enum", title: "Set only when:", required: true, options: ["All Present", "All or Some Present", "Some Present", "Some or None Present", "Not Present", "Any"], defaultValue: "Any"
        input "FilterSun3", "enum", title: "Set only when:", required: true, options: ["Sun Is Up", "Sun Is Down", "Any"], defaultValue: "Any"
    	input "FilterFromMode3", "mode", title: "Set only from these modes:", required: false, multiple: true
    }
    section("Mode 5")
    {
    	input "Mode4", "mode", title: "These rules apply to this mode:", required: false
        input "FilterAfterTime4", "time", title: "Set if after:", required: false
        input "FilterBeforeTime4", "time", title: "Set if before:", required: false
        input "FilterPresence4", "enum", title: "Set only when:", required: true, options: ["All Present", "All or Some Present", "Some Present", "Some or None Present", "Not Present", "Any"], defaultValue: "Any"
        input "FilterSun4", "enum", title: "Set only when:", required: true, options: ["Sun Is Up", "Sun Is Down", "Any"], defaultValue: "Any"
    	input "FilterFromMode4", "mode", title: "Set only from these modes:", required: false, multiple: true
    }
    section("Mode 6")
    {
    	input "Mode5", "mode", title: "These rules apply to this mode:", required: false
        input "FilterAfterTime5", "time", title: "Set if after:", required: false
        input "FilterBeforeTime5", "time", title: "Set if before:", required: false
        input "FilterPresence5", "enum", title: "Set only when:", required: true, options: ["All Present", "All or Some Present", "Some Present", "Some or None Present", "Not Present", "Any"], defaultValue: "Any"
        input "FilterSun5", "enum", title: "Set only when:", required: true, options: ["Sun Is Up", "Sun Is Down", "Any"], defaultValue: "Any"
    	input "FilterFromMode5", "mode", title: "Set only from these modes:", required: false, multiple: true
    }
    section("Mode 7")
    {
    	input "Mode6", "mode", title: "These rules apply to this mode:", required: false
        input "FilterAfterTime6", "time", title: "Set if after:", required: false
        input "FilterBeforeTime6", "time", title: "Set if before:", required: false
        input "FilterPresence6", "enum", title: "Set only when:", required: true, options: ["All Present", "All or Some Present", "Some Present", "Some or None Present", "Not Present", "Any"], defaultValue: "Any"
        input "FilterSun6", "enum", title: "Set only when:", required: true, options: ["Sun Is Up", "Sun Is Down", "Any"], defaultValue: "Any"
    	input "FilterFromMode6", "mode", title: "Set only from these modes:", required: false, multiple: true
    }
    section("Mode 8")
    {
    	input "Mode7", "mode", title: "These rules apply to this mode:", required: false
        input "FilterAfterTime7", "time", title: "Set if after:", required: false
        input "FilterBeforeTime7", "time", title: "Set if before:", required: false
        input "FilterPresence7", "enum", title: "Set only when:", required: true, options: ["All Present", "All or Some Present", "Some Present", "Some or None Present", "Not Present", "Any"], defaultValue: "Any"
        input "FilterSun7", "enum", title: "Set only when:", required: true, options: ["Sun Is Up", "Sun Is Down", "Any"], defaultValue: "Any"
    	input "FilterFromMode7", "mode", title: "Set only from these modes:", required: false, multiple: true
    }
    section("Mode 9")
    {
    	input "Mode8", "mode", title: "These rules apply to this mode:", required: false
        input "FilterAfterTime8", "time", title: "Set if after:", required: false
        input "FilterBeforeTime8", "time", title: "Set if before:", required: false
        input "FilterPresence8", "enum", title: "Set only when:", required: true, options: ["All Present", "All or Some Present", "Some Present", "Some or None Present", "Not Present", "Any"], defaultValue: "Any"
        input "FilterSun8", "enum", title: "Set only when:", required: true, options: ["Sun Is Up", "Sun Is Down", "Any"], defaultValue: "Any"
    	input "FilterFromMode8", "mode", title: "Set only from these modes:", required: false, multiple: true
    }
    section("Mode 10")
    {
    	input "Mode9", "mode", title: "These rules apply to this mode:", required: false
        input "FilterAfterTime9", "time", title: "Set if after:", required: false
        input "FilterBeforeTime9", "time", title: "Set if before:", required: false
        input "FilterPresence9", "enum", title: "Set only when:", required: true, options: ["All Present", "All or Some Present", "Some Present", "Some or None Present", "Not Present", "Any"], defaultValue: "Any"
        input "FilterSun9", "enum", title: "Set only when:", required: true, options: ["Sun Is Up", "Sun Is Down", "Any"], defaultValue: "Any"
    	input "FilterFromMode9", "mode", title: "Set only from these modes:", required: false, multiple: true
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
	state.sunup = true
    state.timezone = -7 //PST
    subscribe(location, "sunriseTime", SunriseTimeHandler)
    subscribe(location, "sunsetTime", SunsetTimeHandler)
	if(PresenceSensors)
    {
	    subscribe(PresenceSensors, "presence", PresenceHandler)
    }
    //def timezonestring = String.format('%04d', state.timezone * 100)
    //log.debug "timezonestring=${timezonestring}"
    
    UpdateMode()
    runEvery5Minutes(UpdateMode)
}

def SunriseTimeHandler(evt)
{
	state.sunup = true
    UpdateMode()
    runEvery5Minutes(UpdateMode)
}

def SunsetTimeHandler(evt)
{
	state.sunup = false
    UpdateMode()
    runEvery5Minutes(UpdateMode)
}

def PresenceHandler(evt)
{
	UpdateMode()
    runEvery5Minutes(UpdateMode)
}

def GetCurrentPresence()
{
	if(!PresenceSensors)
    {
    	return "none"
    }
    int present_count = 0
	for (person in PresenceSensors)
    {
		if (person.currentPresence == "present")
        {
			present_count = present_count + 1
		}
	}
    if(present_count == 0)
    {
    	return "none"
    }
    else if(present_count == PresenceSensors.size())
    {
    	return "all"
    }
	return "some"
}

def CompareTime(time1, time2)
{
	return TimeToInteger(time1) - TimeToInteger(time2)
}

def TimeToInteger(time)
{
    int hour = (time.getHours() + state.timezone) % 24
    if(hour < 0) { hour += 24 }
    int minute = time.getMinutes()
    return (hour * 60) + minute
}

def DoesModeApply(FilterAfterTime, FilterBeforeTime, FilterPresence, FilterSun, FilterFromMode)
{
	Date currenttime = new Date(now())
    if(FilterAfterTime)
    {
    	Date filterdate = toDateTime(FilterAfterTime, null)
        int compareresult = CompareTime(currenttime, filterdate)
        if(compareresult < 0)
        {
        	return false
        }
    }
    if(FilterBeforeTime)
    {
    	Date filterdate = toDateTime(FilterBeforeTime, null)
        int compareresult = CompareTime(currenttime, filterdate)
        if(compareresult >= 0)
        {
        	return false
        }
    }
    if(FilterPresence)
    {
    	def presence = GetCurrentPresence()
        if(FilterPresence == "All Present" && presence != "all")
        {
        	return false
        }
        if(FilterPresence == "All or Some Present" && presence != "all" && presence != "some")
        {
        	return false
        }
        if(FilterPresence == "Some Present" && presence != "some")
        {
        	return false
        }
        if(FilterPresence == "Some or None Present" && presence != "some" && presence != "none")
        {
        	return false
        }
        if(FilterPresence == "Not Present" && presence != "none")
        {
        	return false
        }
    }
    if(FilterSun)
    {
    	if(FilterSun == "Sun Is Up" && !state.sunup)
        {
        	return false
        }
    	if(FilterSun == "Sun Is Down" && state.sunup)
        {
        	return false
        }
    }
    if(FilterFromMode)
    {
    	if(!FilterFromMode.contains(location.mode))
        {
        	return false
        }
    }
    
    return true
}

def GetNextMode()
{
	for(int i = 0; i < 10; i++)
    {
    	def mode = settings["Mode${i}"]
        //log.debug "GetNextMode ${i} ${mode}"
    	if(mode && DoesModeApply(settings["FilterAfterTime${i}"], settings["FilterBeforeTime${i}"], settings["FilterPresence${i}"], settings["FilterSun${i}"], settings["FilterFromMode${i}"]))
        {
        	return mode
        }
    }
    return null
}

def UpdateMode()
{
	def nextmode = GetNextMode()
    //log.debug "UpdateMode -> ${location.mode} -> ${nextmode}"
    if(nextmode && nextmode != location.mode)
    {
    	location.setMode(nextmode)
    }
}