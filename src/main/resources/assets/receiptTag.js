// put tag for receipt id
function putTag(tagName, receiptId){
    $.ajax({
        url: '/tags/' + tagName,
        type: 'put',
        data: JSON.stringify(receiptId),
        contentType: 'application/json',
        dataType: 'json'
    });
}

function displayNewTag(tagName, receiptId){
    var receiptRow = $(".receipt")[receiptId-1];
    var tags = receiptRow.getElementsByClassName("tags");
    var newTagRow = getTagRow(tagName);
    $(newTagRow).appendTo(tags);
}

// put tag name for receipt id on enter key pressed
function search(event, input, receiptId) {
    if(event.keyCode === 13) {
        event.preventDefault();
        var tagName = input.value;
        putTag(tagName, receiptId);
        input.style.display = "none";
        displayNewTag(tagName, receiptId);
    }
}

// add tag to given receipt
function addTagToReceipt(receiptId){
    // get receipt element by id
    var receiptRow = $(".receipt")[receiptId-1];
    var tagInput = `<input class="tag_input" onkeyup="search(event, this, ${receiptId})"/> `;
    $(tagInput).appendTo(receiptRow);
}

// get tag name
function getTagRow(tag){
    return `<span class="tagValue" onclick="deleteTag(this)">${tag} </span>`;
}

// add a receipt to table
function addReceiptToReceiptList(receipt){
    var tagRows = `<td class="tags">`;
    var tagLength = receipt.tags ? receipt.tags.length : 0;
    for (var i=0; i < tagLength; i++){
        tagRows = tagRows + getTagRow(receipt.tags[i]);
    }
    tagRows = tagRows + `</td>`;

    var newReceipt = `<tr class="receipt" id=${receipt.id}>`
        + `<td class="time">${receipt.created}</td>`
        + `<td class="merchant">${receipt.merchant}</td>`
        + `<td class="amount">${receipt.amount}</td>`
        + tagRows
        + `<td class="add-tag" onclick="addTagToReceipt(${receipt.id})">add +</td>`
        + `</tr>`;
    $(newReceipt).appendTo($("#receiptList"));
}

// get receipts
function getReceipts(latest){
    $.getJSON("/receipts", function(receipts){
        if (latest) {
            addReceiptToReceiptList(receipts[receipts.length-1]);
        }
        else {
            for(var i=0; i < receipts.length; i++) {
                var receipt = receipts[i];
                addReceiptToReceiptList(receipt);
            }
        }
    })
}

getReceipts();

// display receipt inputs
function toggleReceiptInputs(show) {
    var receiptInputs = document.getElementById('receiptInputs');
    if (show || receiptInputs.style.display !== "block") {
        receiptInputs.style.display = "block";
    }
    else {
        receiptInputs.style.display = "none";
    }
}

// display camera
function toggleCameraInputs() {
    var cameraInputs = document.getElementById('cameraInputs');
    if (cameraInputs.style.display !== "block") {
        cameraInputs.style.display = "block";
    }
    else {
        cameraInputs.style.display = "none";
    }
}

// cancel receipt inputs
function cancel() {
    document.getElementById("merchant").value = "";
    document.getElementById("amount").value = "";
}

function setReceiptValues(merchant, amount){
    if (merchant) {
        $("#merchant").val(merchant);
    }
    if (amount) {
        $("#amount").val(amount);
    }
        //addReceipt(merchant, amount);
}

// post receipt
function addReceipt(merchant, amount) {
    merchant = document.getElementById("merchant").value;
    amount = parseFloat(document.getElementById("amount").value);
    $.ajax({
        url: '/receipts',
        type: 'post',
        data: JSON.stringify({merchant: merchant, amount: amount}),
        contentType: 'application/json',
        dataType: 'json',
        success: function () {
            getReceipts(true);
        }
    });
    $("#merchant").val("");
    $("#amount").val("");
}

// delete tags
function deleteTag(input) {
    var tagName = input.innerText;
    var receipt = input.parentNode.parentNode;
    var receiptId = receipt.id;
    $.ajax({
        url: '/tags/' + tagName,
        type: 'delete',
        data: JSON.stringify(receiptId),
        contentType: 'application/json',
        dataType: 'json'
    });
    var tags = input.parentNode;
    tags.removeChild(input);
}

var imageCapture;
var track;
function attachMediaStream(mediaStream) {
    $('video')[0].srcObject = mediaStream;
    // Saving the track allows us to capture a photo
    track = mediaStream.getVideoTracks()[0];
    imageCapture = new ImageCapture(track);
}

function startVideo() {
    navigator.mediaDevices.getUserMedia({video: {facingMode: {exact: "environment"}}})
        .then(attachMediaStream)
        .catch(error => {
        navigator.mediaDevices.getUserMedia({video: true})
        .then(attachMediaStream)
        .catch(error => {
        console.log('you are fooked');
        });
    });
}

function takeSnapshot() {
    // create a CANVAS element that is same size as the image
    imageCapture.grabFrame()
        .then(img => {
            const canvas = document.createElement('canvas');
            canvas.width = img.width;
            canvas.height = img.height;
            canvas.getContext('2d').drawImage(img, 0, 0);
            const base64EncodedImageData = canvas.toDataURL('image/png').split(',')[1];
            $.ajax({
                url: "/images",
                type: "POST",
                data: base64EncodedImageData,
                contentType: "text/plain",
                success: function() {},
            })
            .then(response => {
            //$('video').after(`<div>got response: <pre>${JSON.stringify(response)}</pre></div>`);
            merchant = response.merchantName;
            amount = JSON.stringify(response.amount);
            cancelVideo();
            toggleReceiptInputs(true);
            setReceiptValues(merchant, amount);
            })
        .always(() => console.log('request complete'));
    // For debugging, you can uncomment this to see the frame that was captured
    // $('BODY').append(canvas);
    });
}

function cancelVideo() {
    track.stop();
    toggleCameraInputs();
    $('video')[0].srcObject.getVideoTracks()[0].stop();

}

$(function () {
    $('#start-camera').on('click', startVideo);
    $('video').on('play', () => $('#take-pic').prop('disabled', false));
    $('video').on('play', () => $('#take-pic-cancel').prop('disabled', false));
    $('#take-pic').on('click', takeSnapshot);
    $('#take-pic-cancel').on('click', cancelVideo);
});
