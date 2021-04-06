using System;
using System.Threading;
using System.Threading.Tasks;
using System.Text.Json;
using Microsoft.Azure.WebJobs;
using Microsoft.Azure.WebJobs.Host;
using Microsoft.Extensions.Logging;
using Microsoft.Azure.NotificationHubs;
using Microsoft.Azure.NotificationHubs.Messaging;

namespace IoTHubBackend
{
    public static class TemperatureAlertHandler
    {
        [FunctionName("TemperatureAlertHandler")]
        public async static void Run([ServiceBusTrigger("alerts", Connection = "ServiceBusConnectionString")]string myQueueItem, string messageId, ILogger log)
        {
            NotificationHubClient hub = NotificationHubClient
                                            .CreateClientFromConnectionString(GetEnvironmentVariable("NotificationHubConnectionString"), GetEnvironmentVariable("NotificationHubName"), true);

            string notification = JsonSerializer.Serialize(new {
                    title = "IoT Hub Demo notification",
                    body = "Very important notification"
                });
            string message = @"{""notification"": " + notification + @", ""data"": " + myQueueItem + "}";

            log.LogInformation($"About to send message {message}");
            NotificationOutcome outcome = await hub.SendFcmNativeNotificationAsync(message);
            if (outcome != null)
            {
                log.LogInformation(outcome.State.ToString());
                if (outcome.Results != null) {
                    foreach (RegistrationResult result in outcome.Results)
                    {
                        log.LogInformation(result.ApplicationPlatform + "\n" + result.RegistrationId + "\n" + result.Outcome);
                    }
                }
                if (!((outcome.State == Microsoft.Azure.NotificationHubs.NotificationOutcomeState.Abandoned) ||
                    (outcome.State == Microsoft.Azure.NotificationHubs.NotificationOutcomeState.Unknown)))
                {
                    log.LogInformation("Notification delivered");
                }
                if (!string.IsNullOrEmpty(outcome.NotificationId)) {
                    await WaitForThePushStatusAsync(log, hub, outcome);
                }
            }

            log.LogInformation("Function app finished");
        }

        private static string GetEnvironmentVariable(string name)
        {
            return System.Environment.GetEnvironmentVariable(name, EnvironmentVariableTarget.Process);
        }
        private static async Task<NotificationDetails> WaitForThePushStatusAsync(ILogger log, NotificationHubClient nhClient, NotificationOutcome notificationOutcome) 
        {
            var notificationId = notificationOutcome.NotificationId;
            var state = NotificationOutcomeState.Enqueued;
            var count = 0;
            NotificationDetails outcomeDetails = null;
            while ((state == NotificationOutcomeState.Enqueued || state == NotificationOutcomeState.Processing) && ++count < 10)
            {
                try
                {
                    log.LogInformation($"status: {state}");
                    outcomeDetails = await nhClient.GetNotificationOutcomeDetailsAsync(notificationId);
                    state = outcomeDetails.State;
                }
                catch (MessagingEntityNotFoundException)
                {
                    // It's possible for the notification to not yet be enqueued, so we may have to swallow an exception
                    // until it's ready to give us a new state.
                }
                Thread.Sleep(1000);                                                
            }
            return outcomeDetails;
        }
    }
}
