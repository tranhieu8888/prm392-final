using System.Security.Claims;

namespace TeamApp.sln.Utils
{
    public static class HttpContextExt
    {
        public static Guid UserId(this HttpContext ctx)
        {
            var id = ctx.User.FindFirstValue(ClaimTypes.NameIdentifier)
                     ?? ctx.User.FindFirstValue("sub");
            return Guid.Parse(id!);
        }
    }
}
