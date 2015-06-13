function goto_meetme_website() {
    window.location = "http://meetme.com"
}

function popup_download() {
        var dialogInstance = new BootstrapDialog({
                title: '<img src="/static/img/meetme_256.png" alt="" style="width: 64px; height: 64px;" />',
                message: '<b>We hope you enjoy your friend\'s event</b>\nWant to see all your friends\' events, or create your own events and invite your friends?\nDownload meetme today from <a href="http://meetme.com">meetme.com</a>',
                type: BootstrapDialog.TYPE_DEFAULT,
                closable: false,
                buttons: [{
                        label: 'Download meetme',
                        cssClass: 'btn-primary',
                        action: goto_meetme_website
                    }, {
                        label: 'No thanks, continue >>',
                        action: function(dialogItself){
                            dialogItself.close();
                        }
                    }]
            });
            dialogInstance.open();
}

function popup_download_delayed() {
    setTimeout(popup_download, 0);
}

function popup_vote() {
var dialogInstance = new BootstrapDialog({
        title: 'Sorry, can\'t vote',
        message: 'To vote, or to add new suggestions, please download the meetme app.',
        type: BootstrapDialog.TYPE_DEFAULT,
        closable: false,
        buttons: [{
                label: 'Download meetme',
                cssClass: 'btn-primary',
                action: goto_meetme_website
            }, {
                label: 'No thanks, continue >>',
                action: function(dialogItself){
                    dialogItself.close();
                }
            }]
    });
    dialogInstance.open();
}