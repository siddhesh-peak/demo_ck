import 'package:flutter/material.dart';
import 'package:flutter_broadcast_receiver/flutter_broadcast_receiver.dart';

import 'package:flutter_incoming_call/flutter_incoming_call.dart';
import 'package:uuid/uuid.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(title: 'Flutter Demo Home Page'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key, required this.title}) : super(key: key);

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  var uuid = const Uuid();

  BaseCallEvent? _lastEvent;
  CallEvent? _lastCallEvent;
  HoldEvent? _lastHoldEvent;
  MuteEvent? _lastMuteEvent;
  DmtfEvent? _lastDmtfEvent;
  AudioSessionEvent? _lastAudioSessionEvent;

  void _incomingCall() {
    final uid = uuid.v4();
    const name = 'Daenerys Targaryen';
    const avatar =
        'https://scontent.fhel6-1.fna.fbcdn.net/v/t1.0-9/62009611_2487704877929752_6506356917743386624_n.jpg?_nc_cat=102&_nc_sid=09cbfe&_nc_ohc=cIgJjOYlVj0AX_J7pnl&_nc_ht=scontent.fhel6-1.fna&oh=ef2b213b74bd6999cd74e3d5de235cf4&oe=5F6E3331';
    const handle = 'example_incoming_call';
    const type = HandleType.generic;
    const isVideo = true;
    FlutterIncomingCall.displayIncomingCall(
        uid, name, avatar, handle, type, isVideo);
  }

  void _endCurrentCall() {
    if (_lastEvent != null) {
      FlutterIncomingCall.endCall(_lastCallEvent!.uuid);
    }
  }

  void _endAllCalls() {
    FlutterIncomingCall.endAllCalls();
  }

  registerBroadcast() {
    /// register
    const String keyMessage = 'phone_call_rec';

    /// Subscription Example
    BroadcastReceiver().subscribe<String> // Data Type returned from publisher
        (keyMessage, (message) {
      print('Printing broadcast SiD : $message');
    });
  }

  @override
  void initState() {
    super.initState();

    registerBroadcast();

    FlutterIncomingCall.configure(
        appName: 'example_incoming_call',
        duration: 30000,
        android: ConfigAndroid(
          vibration: true,
          ringtonePath: 'default',
          channelId: 'calls',
          channelName: 'Calls channel name',
          channelDescription: 'Calls channel description',
        ),
        ios: ConfigIOS(
          iconName: 'AppIcon40x40',
          ringtonePath: null,
          includesCallsInRecents: false,
          supportsVideo: true,
          maximumCallGroups: 2,
          maximumCallsPerCallGroup: 1,
        ));
    FlutterIncomingCall.onEvent.listen((event) {
      setState(() {
        _lastEvent = event;
      });
      if (event is CallEvent) {
        setState(() {
          _lastCallEvent = event;
        });
      } else if (event is HoldEvent) {
        setState(() {
          _lastHoldEvent = event;
        });
      } else if (event is MuteEvent) {
        setState(() {
          _lastMuteEvent = event;
        });
      } else if (event is DmtfEvent) {
        setState(() {
          _lastDmtfEvent = event;
        });
      } else if (event is AudioSessionEvent) {
        setState(() {
          _lastAudioSessionEvent = event;
        });
      }
    });
  }

  @override
  void dispose() {
    _endAllCalls();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Call Kit'),
      ),
      body: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.max,
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: <Widget>[
            TextButton(
              child: const Text('Incoming call now'),
              onPressed: _incomingCall,
            ),
            const SizedBox(height: 16),
            TextButton(
              child: const Text('Incoming call delay 5 sec'),
              onPressed: () =>
                  Future.delayed(const Duration(seconds: 5), _incomingCall),
            ),
            const SizedBox(height: 16),
            TextButton(
              child: const Text('End current call'),
              onPressed: _endCurrentCall,
            ),
            const SizedBox(height: 16),
            TextButton(
              child: const Text('End all calls'),
              onPressed: _endAllCalls,
            ),
            const SizedBox(height: 16),
            const Text(
              'Last event:',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
            ),
            Text(
              _lastEvent != null ? _lastEvent.toString() : 'Not event',
              style: const TextStyle(fontSize: 16),
            ),
            if (_lastCallEvent != null) ...[
              const Text(
                'Last call event:',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Text(
                _lastCallEvent.toString(),
                style: const TextStyle(fontSize: 16),
              )
            ],
            if (_lastHoldEvent != null) ...[
              const Text(
                'Last hold event:',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Text(
                _lastHoldEvent.toString(),
                style: const TextStyle(fontSize: 16),
              )
            ],
            if (_lastMuteEvent != null) ...[
              const Text(
                'Last mute event:',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Text(
                _lastMuteEvent.toString(),
                style: const TextStyle(fontSize: 16),
              )
            ],
            if (_lastDmtfEvent != null) ...[
              const Text(
                'Last dmtf event:',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Text(
                _lastDmtfEvent.toString(),
                style: const TextStyle(fontSize: 16),
              )
            ],
            if (_lastAudioSessionEvent != null) ...[
              const Text(
                'Last audio session event:',
                style: TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.bold,
                ),
              ),
              Text(
                _lastAudioSessionEvent.toString(),
                style: const TextStyle(fontSize: 16),
              )
            ]
          ],
        ),
      ),
    );
  }
}
