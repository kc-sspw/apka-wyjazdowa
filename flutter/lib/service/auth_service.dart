import 'package:firebase_auth/firebase_auth.dart';
import 'package:google_sign_in/google_sign_in.dart';
import 'package:flutter/foundation.dart' show kIsWeb;

/// Author: Łukasz Piętka (FUT 2025)
class AuthService {
  final FirebaseAuth _auth = FirebaseAuth.instance;
  final GoogleSignIn _googleSignIn = GoogleSignIn();



  Future<void> signInWithGoogle() async {
    if (kIsWeb) {
      // --- LOGIKA DLA FLUTTER WEB ---
      GoogleAuthProvider googleProvider = GoogleAuthProvider();

      // Opcjonalnie: możesz dodać wymuszenie wyboru konta
      googleProvider.setCustomParameters({'prompt': 'select_account'});

      // Otwarcia okna popup w przeglądarce
      await _auth.signInWithPopup(googleProvider);
    } else {
      // --- LOGIKA DLA ANDROID / IOS ---
      final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
      if (googleUser == null) {
        await signOut();
        throw Exception('Logowanie anulowane');
      }

      final googleAuth = await googleUser.authentication;

      final credential = GoogleAuthProvider.credential(
        accessToken: googleAuth.accessToken,
        idToken: googleAuth.idToken,
      );

      await _auth.signInWithCredential(credential);
    }
  }

  /*Future<void> signInWithGoogle() async {
    final GoogleSignInAccount? googleUser = await _googleSignIn.signIn();
    if (googleUser == null) {
      await signOut();
      throw Exception('Logowanie anulowane');
    }

    final googleAuth = await googleUser.authentication;

    final credential = GoogleAuthProvider.credential(
      accessToken: googleAuth.accessToken,
      idToken: googleAuth.idToken,
    );

    await _auth.signInWithCredential(credential);
  }*/

  Future<void> signOut() async {
    await _googleSignIn.signOut();
    await _auth.signOut();
  }
}
