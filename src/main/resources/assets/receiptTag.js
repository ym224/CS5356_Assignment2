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
    return `<span class="tagValue" onclick="deleteTag(this)">${tag}</span>`;
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
function displayReceiptInputs() {
    document.getElementById('receiptInputs').style.display = "block";
}

// cancel receipt inputs
function cancel() {
    document.getElementById("merchant").value = "";
    document.getElementById("amount").value = "";
}

// post receipt
function addReceipt() {
    var merchant = document.getElementById("merchant").value;
    var amount = parseFloat(document.getElementById("amount").value);
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