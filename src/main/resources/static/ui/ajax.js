$(document).ready(function () {
    $("#button").click(function (e) {
        var url = $('#text').val();
        if (url.length > 0) {
            $("#button").attr("disabled", true);
            $("#text").attr("disabled", true);
            $('#message').html("");
            $( "#loading" ).show();
            $.ajax({
                type: "POST",
                url: "create",
                data: {"data" : url},
                dataType: "json",
                success: function (result, status, xhr) {
                    $("#message").html(result["short"]);
                },
                error: function (xhr, status, error) {
                    $("#message").html("Error: " + status + " " + error + " " + xhr.status + " " + xhr.statusText);
                },
                complete: function() {
                   $('#loading').hide();
                   $("#button").attr("disabled", false);
                   $("#text").attr("disabled", false);
                }
            });
        }
    });
});
