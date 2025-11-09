namespace TeamApp.Application.RealTime
{
    /// <summary>
    /// Abstraction để phát realtime ra client (VD: SignalR).
    /// Implement ở Web/API layer.
    /// </summary>
    public interface IRealtimeNotifier
    {
        /// <summary>
        /// Gửi payload tới một conversation (group) theo Id.
        /// </summary>
        Task SendToConversationAsync(Guid conversationId, string method, object payload);
    }
}
