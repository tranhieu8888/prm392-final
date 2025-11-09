using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using TeamApp.Application;
using TeamApp.sln.Utils;

namespace TeamApp.Api.Controllers;

[ApiController, Route("api/calendar")]
public class CalendarController : ControllerBase
{
    private readonly CalendarService _svc;
    public CalendarController(CalendarService svc) { _svc = svc; }

    [HttpGet("tasks"), Authorize]
    public Task<List<TaskDto>> TasksInMonth([FromQuery] int year, [FromQuery] int month)
        => _svc.TasksInMonthAsync(HttpContext.UserId(), year, month);
}
