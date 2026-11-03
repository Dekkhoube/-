import os
import mimetypes
from http.server import HTTPServer, BaseHTTPRequestHandler

def find_apk_path():
    candidates = [
        "/app/applet/.build-outputs/noor-islamic-release.apk",
        "/app/applet/.build-outputs/noor-islamic.apk",
        "/app/applet/app/build/outputs/apk/release/app-release.apk",
        "/app/applet/.build-outputs/app-release.apk",
        ".build-outputs/noor-islamic-release.apk",
        ".build-outputs/noor-islamic.apk",
        "app/build/outputs/apk/release/app-release.apk",
        ".build-outputs/app-debug.apk",
        "/app/applet/.build-outputs/app-debug.apk",
        "/app/applet/app/build/outputs/apk/debug/app-debug.apk"
    ]
    for c in candidates:
        if os.path.exists(c) and os.path.getsize(c) > 1000000:
            return c
    return candidates[0]

HTML_CONTENT = """<!DOCTYPE html>
<html lang="bn">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>নূর ইসলামিক - APK ডাউনলোড</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
        }
        body {
            background: linear-gradient(135deg, #064e3b 0%, #022c22 100%);
            min-height: 100vh;
            color: #ffffff;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 16px;
        }
        .card {
            background: rgba(255, 255, 255, 0.08);
            backdrop-filter: blur(16px);
            -webkit-backdrop-filter: blur(16px);
            border: 1px solid rgba(217, 119, 6, 0.35);
            border-radius: 24px;
            padding: 28px 20px;
            max-width: 450px;
            width: 100%;
            text-align: center;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.45);
        }
        .app-icon {
            width: 84px;
            height: 84px;
            background: linear-gradient(135deg, #059669 0%, #047857 100%);
            border-radius: 22px;
            margin: 0 auto 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 8px 24px rgba(5, 150, 105, 0.45);
            border: 2px solid #fbbf24;
            font-size: 40px;
        }
        h1 {
            font-size: 26px;
            font-weight: 800;
            color: #fef3c7;
            margin-bottom: 6px;
        }
        .subtitle {
            font-size: 14px;
            color: #a7f3d0;
            margin-bottom: 18px;
            font-weight: 500;
        }
        .badge-row {
            display: flex;
            justify-content: center;
            gap: 8px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }
        .badge {
            background: rgba(255, 255, 255, 0.12);
            padding: 5px 12px;
            border-radius: 18px;
            font-size: 12px;
            color: #fde68a;
            border: 1px solid rgba(253, 230, 138, 0.25);
        }
        .btn-download {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 12px;
            background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
            color: #111827;
            text-decoration: none;
            padding: 16px 20px;
            border-radius: 16px;
            font-size: 17px;
            font-weight: 800;
            box-shadow: 0 8px 20px rgba(217, 119, 6, 0.4);
            transition: all 0.2s ease;
            margin-bottom: 20px;
        }
        .btn-download:active {
            transform: scale(0.98);
        }
        .info-box {
            background: rgba(0, 0, 0, 0.28);
            border-radius: 16px;
            padding: 16px;
            text-align: left;
            margin-bottom: 16px;
            border: 1px solid rgba(255, 255, 255, 0.08);
        }
        .info-title {
            font-size: 14px;
            font-weight: 700;
            color: #fef08a;
            margin-bottom: 8px;
            display: flex;
            align-items: center;
            gap: 6px;
        }
        .info-list {
            font-size: 13px;
            color: #e2e8f0;
            line-height: 1.6;
            padding-left: 18px;
        }
        .info-list li {
            margin-bottom: 6px;
        }
        .features-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 8px;
            font-size: 12px;
            color: #cbd5e1;
            margin-top: 10px;
            text-align: left;
        }
        .feature-item {
            display: flex;
            align-items: center;
            gap: 6px;
        }
        .check {
            color: #34d399;
            font-weight: bold;
        }
        .footer {
            font-size: 11px;
            color: #94a3b8;
            margin-top: 16px;
        }
    </style>
</head>
<body>
    <div class="card">
        <div class="app-icon">🌙</div>
        <h1>নূর ইসলামিক</h1>
        <div class="subtitle">Noor Islamic Official Android Release</div>

        <div class="badge-row">
            <span class="badge">সাইন্ড রিলিজ APK</span>
            <span class="badge">সংস্করণ ১.০</span>
            <span class="badge">২২ মেগাবাইট</span>
            <span class="badge">Android 7.0+</span>
            <span class="badge">১০০% অফলাইন</span>
        </div>

        <a href="/download/noor-islamic.apk" download="noor-islamic.apk" class="btn-download">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path>
                <polyline points="7 10 12 15 17 10"></polyline>
                <line x1="12" y1="15" x2="12" y2="3"></line>
            </svg>
            <span>সরাসরি APK ডাউনলোড করুন</span>
        </a>

        <div class="info-box">
            <div class="info-title">
                📲 মোবাইলে ইনস্টল করার সহজ নিয়ম:
            </div>
            <ol class="info-list">
                <li>ওপরের <strong>"সরাসরি APK ডাউনলোড করুন"</strong> বাটনে ট্যাপ করুন।</li>
                <li>ডাউনলোড শেষ হলে মোবাইলের নোটিফিকেশন বার থেকে অথবা <strong>Files / Downloads</strong> ফোল্ডারে গিয়ে ফাইলে ট্যাপ করুন।</li>
                <li>যদি <em>"Install unknown apps"</em> অনুমতি চায়, তবে <strong>Allow (অনুমতি)</strong> দিন।</li>
                <li>এরপর <strong>Install</strong> চাপলেই অ্যাপটি ইনস্টল হয়ে যাবে! কোনো লগইন লাগবে না।</li>
            </ol>
        </div>

        <div class="info-box">
            <div class="info-title">
                ✨ অ্যাপের মূল সুবিধাসমূহ:
            </div>
            <div class="features-grid">
                <div class="feature-item"><span class="check">✓</span> ৩০ পারা হাফেজী কুরআন</div>
                <div class="feature-item"><span class="check">✓</span> ৯ জন ক্বারীর অডিও</div>
                <div class="feature-item"><span class="check">✓</span> Gemini এআই সহকারী</div>
                <div class="feature-item"><span class="check">✓</span> সঠিক কিবলা কম্পাস</div>
                <div class="feature-item"><span class="check">✓</span> সহীহ হাদিস ও দু'আ ভাণ্ডার</div>
                <div class="feature-item"><span class="check">✓</span> ৫ ওয়াক্ত নামাজের সময়সূচি</div>
            </div>
        </div>

        <div class="footer">
            নূর ইসলামিক • Production Release<br>
            Secure Cloud-Built Android Package
        </div>
    </div>
</body>
</html>"""

class ApkDownloadHandler(BaseHTTPRequestHandler):
    def do_HEAD(self):
        self.do_GET()
    def do_GET(self):
        clean_path = self.path.split("?")[0]
        if clean_path in ["/download/noor-islamic.apk", "/app-debug.apk", "/noor-islamic.apk", "/app-release.apk"]:
            apk_file = find_apk_path()
            if os.path.exists(apk_file):
                file_size = os.path.getsize(apk_file)
                self.send_response(200)
                self.send_header("Content-Type", "application/vnd.android.package-archive")
                self.send_header("Content-Disposition", 'attachment; filename="noor-islamic.apk"')
                self.send_header("Content-Length", str(file_size))
                self.send_header("Cache-Control", "no-cache")
                self.end_headers()
                with open(apk_file, "rb") as f:
                    while chunk := f.read(65536):
                        self.wfile.write(chunk)
            else:
                self.send_response(404)
                self.end_headers()
                self.wfile.write(b"APK file not found on server.")
        else:
            encoded = HTML_CONTENT.encode("utf-8")
            self.send_response(200)
            self.send_header("Content-Type", "text/html; charset=utf-8")
            self.send_header("Content-Length", str(len(encoded)))
            self.end_headers()
            self.wfile.write(encoded)

    def log_message(self, format, *args):
        pass

def run():
    server_address = ('', 3000)
    httpd = HTTPServer(server_address, ApkDownloadHandler)
    print("Serving APK download web server on port 3000...")
    httpd.serve_forever()

if __name__ == '__main__':
    run()
