class ApiConfig {
  static const ipPort = "zerowapka.wittydune-52e2f384.polandcentral.azurecontainerapps.io";
  //static const ipPort = "192.168.0.80:8080";
  static final baseURL = "https://$ipPort/";
  static final baseAPIURL = "${baseURL}api/";
  static final wssAPIURL = "wss://$ipPort/api/ws";

  static String resolveImage(String path) {
    if (path.startsWith('http')) return path;
    return baseURL + path.replaceFirst(RegExp(r'^/'), '');
  }
}