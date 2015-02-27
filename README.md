# android-sms-sync
Aims to synchronize sms of an Android phone with a PIMS using a WS.

## How does it work?

You configure the URL of the web service (https is recommended) and, when appropriate, a service key.

When synchronization has been started, all messages are read and sent to the specified web service using POST requests, their body containing JSON representing an array of messages. Each array contains as many messages as defined using the "chunk size" pref. The JSON object is roughly equals to the Android message structure.

## TODO

+ Use Gradle instead of being a raw eclipse project.
+ Some kind of optimizations can be brought when retrieving messages.
+ On the fly synchronization.
