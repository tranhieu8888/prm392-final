using Microsoft.Extensions.Configuration;
using System.Net.Http.Headers;
using System.Text;
using System.Text.Json;

namespace TeamApp.Infrastructure;

public class FcmSender
{
    private readonly HttpClient _http;
    private readonly string _serverKey; // Legacy key cho demo

    public FcmSender(HttpClient http, IConfiguration cfg)
    {
        _http = http;
        _serverKey = cfg["Fcm:ServerKey"] ?? "";
    }

    public async Task SendAsync(string deviceToken, string title, string body, Dictionary<string, string>? data = null)
    {
        if (string.IsNullOrWhiteSpace(_serverKey) || string.IsNullOrWhiteSpace(deviceToken)) return;

        var payload = new
        {
            to = deviceToken,
            notification = new { title, body },
            data = data ?? new Dictionary<string, string>()
        };
        var req = new HttpRequestMessage(HttpMethod.Post, "https://fcm.googleapis.com/fcm/send");
        req.Headers.Authorization = new AuthenticationHeaderValue("key", "=" + _serverKey);
        req.Content = new StringContent(JsonSerializer.Serialize(payload), Encoding.UTF8, "application/json");
        await _http.SendAsync(req);
    }
}
