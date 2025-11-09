using Microsoft.AspNetCore.SignalR;
using TeamApp.Application.RealTime;

namespace TeamApp.Api.Realtime
{
    /// <summary>
    /// Implement IRealtimeNotifier bằng SignalR Hub.
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
            var group = conversationId.ToString();
            return _hub.Clients.Group(group).SendAsync(method, payload);
        }
    }
}
