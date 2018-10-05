console.log('Loading function');
const request = require('request');
const aws = require('aws-sdk');
const s3 = new aws.S3({ apiVersion: '2006-03-01' });
const S = require('string');

exports.handler = async (event, context) => {
  console.log('Received event:', JSON.stringify(event, null, 2));

  // Get the object from the event and show its content type
  const bucket = event.Records[0].s3.bucket.name;
  const key = decodeURIComponent(event.Records[0].s3.object.key.replace(/\+/g, ' '));
  const versionId = event.Records[0].s3.object.versionId;
  console.log('bucket: ' + bucket);
  console.log('key: ' + key);

  const updateCategoryPath = process.env.MYBEAUTIP_S3_DEV_UPDATE_CATEGORY_ENDPOINT;
  const updateStoreCoverPath = process.env.MYBEAUTIP_S3_DEV_UPDATE_STORE_COVER_ENDPOINT;
  const updateStoreThumbnailPath = process.env.MYBEAUTIP_S3_DEV_UPDATE_STORE_THUMBNAIL_ENDPOINT;
  const updateStoreRefundPath = process.env.MYBEAUTIP_S3_DEV_UPDATE_STORE_REFUND_ENDPOINT;
  const updateStoreAsPath = process.env.MYBEAUTIP_S3_DEV_UPDATE_STORE_AS_ENDPOINT;
  let requestUri;
  let requestBody;

  if (key.includes("category")) {
    const categoryId = S(key).between("category/", ".png").s;
    console.log(S(key).between("category/", ".png").s)
    requestUri = updateCategoryPath + categoryId;
  }

  if (key.includes("store")) {
    const storeId = S(key).between("store/", "_").s;

    if (key.includes("cover")) {
      requestUri = updateStoreCoverPath + storeId;
    }
    if (key.includes("thumbnail")) {
      requestUri = updateStoreThumbnailPath + storeId;
    }
    if (key.includes("as")) {
      requestUri = updateStoreAsPath + storeId;
    }
    if (key.includes("refund")) {
      requestUri = updateStoreRefundPath + storeId;
    }
  }

  const myBeautipRequest = request.defaults({
    headers: {'Content-Type': 'application/json',
              'Authorization': 'Bearer ' + process.env.MYBEAUTIP_S3_DEV_TOKEN}
  });

  console.log('myBeautipUpdateImageURL Request:' + requestUri);
  myBeautipRequest.patch(requestUri, null, function (err, response) {
    if (err) {
      console.log('myBeautipUpdateImageURL Response:' + requestUri + ':' + response.statusCode);
      console.log(err);
    } else {
      console.log('myBeautipUpdateImageURL Response:' + requestUri + ':' + response.statusCode);
    }
  });
};
