# Naggle

Yes, it's another to-do list app. But this one is so annoying you just might get stuff done.

Inspired by my experience as a new parent. Sometimes you randomly have 15 minutes to get something done. Wouldn't it be nice to have a list you could swipe through until you find the right task for the moment?

# Basic Functionality
* Tap the FAB to add a new task with description, priority, and start time
* The app will always present a local notification with your top priority todo item
* Dismissing a todo will send a notification with the next item on your list
* Once you hit the start date for a new notification, that will be added to the mix
* Mark todo items complete when you've done them, or keep swiping through notifications until you get to one you're actually ready to do at this moment

# Architecture
* MVVM & databinding
* Room for data storage, local-only for now
* Kotlin coroutines
* Koin for dependency injection
