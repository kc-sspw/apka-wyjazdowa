import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:zerowapka/model/crew.dart';
import 'package:zerowapka/service/crew_service.dart';
import '../service/dio_service.dart';

/// Author: Łukasz Piętka (FUT 2025)
class AuthProvider with ChangeNotifier {
  final FirebaseAuth _auth = FirebaseAuth.instance;
  User? user;
  String? idToken;
  Crew? me;

  AuthProvider() {
    _auth.authStateChanges().listen(_onAuthStateChanged);
  }

  /*Future<void> _onAuthStateChanged(User? newUser) async {
    user = newUser;
    if (user != null) {
      idToken = await user!.getIdToken(true);
      DioService().setIdToken(idToken!);
      me = await reloadMe();
      if (me == null) {
        idToken = null;
        DioService().clearIdToken();
        await newUser?.delete();
        print('Invalid user, deleting...');
      }
    } else {
      idToken = null;
      me = null;
      DioService().clearIdToken();
    }
    notifyListeners();
  }*/

  Future<void> _onAuthStateChanged(User? newUser) async {
    user = newUser;
    if (user != null) {
      try {
        idToken = await user!.getIdToken(true);
        DioService().setIdToken(idToken!);

        // --- TUTAJ DODAJEMY TRY-CATCH ---
        print('Wysyłam zapytanie do backendu...');
        me = await reloadMe();
        print('Sukces! Pobrano dane użytkownika: ${me?.name}');

      } catch (e) {
        // Wydrukuje dokładną przyczynę (np. brak połączenia, błędny IP, Firewall)
        print('====================================================');
        print('BŁĄD POŁĄCZENIA Z BACKENDEM: $e');
        print('====================================================');
        me = null;
      }

      if (me == null) {
        idToken = null;
        DioService().clearIdToken();

        // TYMCZASOWO ZAKOMENTUJ PONIŻSZĄ LINIĘ, żeby aplikacja nie usuwała klastra z Firebase
        // await newUser?.delete();

        print('Invalid user, nie udało się pobrać danych z backendu.');
      }
    } else {
      idToken = null;
      me = null;
      DioService().clearIdToken();
    }
    notifyListeners();
  }

  Future<Crew?> reloadMe() {
    return CrewService().getMe(forceRefresh: true);
  }

  Future<void> refreshToken() async {
    if (user != null) {
      idToken = await user!.getIdToken(true);
      DioService().setIdToken(idToken!);
      notifyListeners();
    }
  }
}
