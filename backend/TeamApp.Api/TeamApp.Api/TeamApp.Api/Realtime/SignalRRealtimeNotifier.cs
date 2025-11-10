using Microsoft.AspNetCore.SignalR;
using TeamApp.Application.RealTime;

namespace TeamApp.Api.Realtime;

/// <summary>
/// Cầu nối giữa tầng ứng dụng (Application) và SignalR Hub.
/// </summary>
public class SignalRRealtimeNotifier : IRealtimeNotifier
{
    private readonly IHubContext<ChatHub> _hub;

    public SignalRRealtimeNotifier(IHubContext<ChatHub> hub)
    {
        _hub = hub;
    }

    public Task SendToConversationAsync(Guid conversationId, string method, object payload)
    {
        string group = conversationId.ToString();
        // Gửi đến tất cả trong group (kể cả người gửi)
        return _hub.Clients.Group(group).SendAsync(method, payload);
    }
}
