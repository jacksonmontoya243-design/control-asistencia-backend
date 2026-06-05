function onScanSuccess(decodedText) {

    document
        .getElementById("resultadoQR")
        .innerHTML =
        "✅ " + decodedText;

    console.log(decodedText);
}

const html5QrCode =
    new Html5Qrcode("reader");

Html5Qrcode.getCameras()
    .then(devices => {

        if(devices && devices.length){

            html5QrCode.start(

                devices[0].id,

                {
                    fps: 10,
                    qrbox: 250
                },

                onScanSuccess
            );
        }
    });