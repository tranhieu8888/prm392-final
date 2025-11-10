using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.SignalR;

namespace TeamApp.Api;

[Authorize]
public class ChatHub : Hub
{
    public Task JoinConversation(string conversationId)
        => Groups.AddToGroupAsync(Context.ConnectionId, conversationId);

    public Task LeaveConversation(string conversationId)
        => Groups.RemoveFromGroupAsync(Context.ConnectionId, conversationId);
}
