const request = require('request');
const aws = require('aws-sdk');
const s3 = new aws.S3({ apiVersion: '2006-03-01' });
const S = require('string');

exports.handler = (event, context, callback) => {
  console.log('Received event:', JSON.stringify(event, null, 2));

  // Get the object from the event and show its content type
  const bucket = event.Records[0].s3.bucket.name;
  const key = decodeURIComponent(event.Records[0].s3.object.key.replace(/\+/g, ' '));

  const updateDevCategoryPath = process.env.MYBEAUTIP_S3_DEV_UPDATE_CATEGORY_ENDPOINT;
  const updateDevStoreCoverPath = process.env.MYBEAUTIP_S3_DEV_UPDATE_STORE_COVER_ENDPOINT;
  const updateDevStoreThumbnailPath = process.env.MYBEAUTIP_S3_DEV_UPDATE_STORE_THUMBNAIL_ENDPOINT;

  const updateProdCategoryPath = process.env.MYBEAUTIP_S3_PROD_UPDATE_CATEGORY_ENDPOINT;
  const updateProdStoreCoverPath = process.env.MYBEAUTIP_S3_PROD_UPDATE_STORE_COVER_ENDPOINT;
  const updateProdStoreThumbnailPath = process.env.MYBEAUTIP_S3_PROD_UPDATE_STORE_THUMBNAIL_ENDPOINT;

  let requestDevUri, requestProdUri;

  if (key.includes("category")) {
    const categoryId = S(key).between("category/", ".png").s;
    console.log(S(key).between("category/", ".png").s)
    requestDevUri = updateDevCategoryPath + categoryId;
    requestProdUri = updateProdCategoryPath + categoryId;
  }

  if (key.includes("store")) {
    const storeId = S(key).between("store/", "_").s;

    if (key.includes("cover")) {
      requestDevUri = updateDevStoreCoverPath + storeId;
      requestProdUri = updateProdStoreCoverPath + storeId;
    }
    if (key.includes("thumbnail")) {
      requestDevUri = updateDevStoreThumbnailPath + storeId;
      requestProdUri = updateProdStoreThumbnailPath + storeId;
    }
  }

  updateDevImageUrl(requestDevUri);
  updateProdImageUrl(requestProdUri);
}

function updateDevImageUrl(requestUri) {
  const myBeautipRequest = request.defaults({
    headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + process.env.MYBEAUTIP_S3_DEV_TOKEN}
  });

  console.log('myBeautipDevUpdateImageURL Request:' + requestUri);
  myBeautipRequest.patch(requestUri, null, function (err, response, body) {
    if (err) {
      console.log('myBeautipDevUpdateImageURL Response:' + requestUri + ':' + response.statusCode);
      console.log(err);
    } else {
      console.log('myBeautipDevUpdateImageURL Response:' + requestUri + ':' + response.statusCode);
    }
  });
}

function updateProdImageUrl(requestUri) {
  const myBeautipRequest = request.defaults({
    headers: {'Content-Type': 'application/json', 'Authorization': 'Bearer ' + process.env.MYBEAUTIP_S3_PROD_TOKEN}
  });

  console.log('myBeautipProdUpdateImageURL Request:' + requestUri);
  myBeautipRequest.patch(requestUri, null, function (err, response, body) {
    if (err) {
      console.log('myBeautipProdUpdateImageURL Response:' + requestUri + ':' + response.statusCode);
      console.log(err);
    } else {
      console.log('myBeautipProdUpdateImageURL Response:' + requestUri + ':' + response.statusCode);
    }
  });
}
