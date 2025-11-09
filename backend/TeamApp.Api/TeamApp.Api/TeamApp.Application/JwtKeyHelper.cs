// JwtKeyHelper.cs
using System.Text;
using Microsoft.Extensions.Configuration;

namespace TeamApp.Application;

public static class JwtKeyHelper
{
    public static byte[] GetKeyBytes(IConfiguration cfg)
    {
        var secretUtf8 = cfg["Jwt:Secret"];
        var secretBase64 = cfg["Jwt:SecretBase64"];

        byte[] keyBytes;

        if (!string.IsNullOrWhiteSpace(secretBase64))
        {
            keyBytes = Convert.FromBase64String(secretBase64);
        }
        else if (!string.IsNullOrWhiteSpace(secretUtf8))
        {
            // Tự nhận diện nếu Secret thực ra là Base64
            bool looksLikeB64 = secretUtf8.EndsWith("=") && secretUtf8.All(c => char.IsLetterOrDigit(c) || c == '+' || c == '/' || c == '=');
            keyBytes = looksLikeB64 ? Convert.FromBase64String(secretUtf8)
                                    : Encoding.UTF8.GetBytes(secretUtf8);
        }
        else
        {
            throw new ArgumentNullException("Jwt:Secret / Jwt:SecretBase64", "Thiếu secret trong cấu hình.");
        }

        if (keyBytes.Length < 32) // 256-bit cho HS256
            throw new ArgumentOutOfRangeException(nameof(keyBytes), $"JWT secret phải >= 32 bytes. Hiện tại: {keyBytes.Length}.");

        return keyBytes;
    }
}
