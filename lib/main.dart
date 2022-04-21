import 'package:flutter/material.dart';
import 'package:flutter_callkeep/flutter_callkeep.dart';
import 'package:permission_handler/permission_handler.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await CallKeep.setup();

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
  Future<void> displayIncomingCall(BuildContext context) async {
    Map<Permission, PermissionStatus> statuses = await [
      Permission.contacts,
      Permission.phone,
    ].request();

    await CallKeep.askForPermissionsIfNeeded(context);
    const callUUID = '0783a8e5-8353-4802-9448-c6211109af51';
    const number = '243789321';

    await CallKeep.displayIncomingCall(
        callUUID, number, 'Meet Shah', HandleType.number, true);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          children: <Widget>[
            ElevatedButton(
              child: const Text('Display incoming call'),
              onPressed: () => displayIncomingCall(context),
            )
          ],
        ),
      ),
    );
  }
}
