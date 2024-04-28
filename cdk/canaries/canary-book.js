var synthetics = require('Synthetics');
const log = require('SyntheticsLogger');

const recordedScript = async function () {
  let page = await synthetics.getPage();

  const navigationPromise = page.waitForNavigation()

  await synthetics.executeStep('Go to home page', async function() {
    await page.goto(process.env.TARGET_URL, {waitUntil: 'domcontentloaded', timeout: 30000})
  })

  await page.setViewport({ width: 1920, height: 961 })

  await synthetics.executeStep('Click "Login" button', async function() {
    await page.waitForSelector('html')
    await page.click('html')
  })

  await navigationPromise

  await synthetics.executeStep('Type username', async function() {
    await page.type('div:nth-child(2) > div > div > .cognito-asf #signInFormUsername', process.env.USER_NAME)
  })

  await synthetics.executeStep('Type password', async function() {
    await page.type('div:nth-child(2) > div > div > .cognito-asf #signInFormPassword', process.env.PASSWORD)
  })

  await navigationPromise

  await synthetics.executeStep('Switch to dark mode', async function() {
    await page.waitForSelector('.container #darkSwitch')
    await page.click('.container #darkSwitch')
  })

  await synthetics.executeStep('Change dark mode', async function() {
    await page.type('.container #darkSwitch', "on")
  })

  await synthetics.executeStep('Click on navigation button', async function() {
    await page.waitForSelector('div > nav > .pagination > .page-item:nth-child(1) > .page-link')
    await page.click('div > nav > .pagination > .page-item:nth-child(1) > .page-link')
  })

  await navigationPromise

  await synthetics.executeStep('Click on navigation button', async function() {
    await page.waitForSelector('div > nav > .pagination > .page-item:nth-child(2) > .page-link')
    await page.click('div > nav > .pagination > .page-item:nth-child(2) > .page-link')
  })

  await navigationPromise

  await synthetics.executeStep('Click "Show available books" button', async function() {
    await page.waitForSelector('.row #availableBooksButton')
    await page.click('.row #availableBooksButton')
  })

  await navigationPromise

  await synthetics.executeStep('Click "Show borrowed books" button', async function() {
    await page.waitForSelector('.row #borrowedBooksButton')
    await page.click('.row #borrowedBooksButton')
  })

  await navigationPromise

  await synthetics.executeStep('Click "Offer" button', async function() {
    await page.waitForSelector('tbody > tr:nth-child(1) > td > .dropdown > .btn')
    await page.click('tbody > tr:nth-child(1) > td > .dropdown > .btn')
  })

  await synthetics.executeStep('Choose whom the book offer to', async function() {
    await page.waitForSelector('td > .dropdown > .show > form:nth-child(2) > .dropdown-item')
    await page.click('td > .dropdown > .show > form:nth-child(2) > .dropdown-item')
  })

  await navigationPromise

  await synthetics.executeStep('Click "Show all books" button', async function() {
    await page.waitForSelector('.row #allBooksButton')
    await page.click('.row #allBooksButton')
  })

  await navigationPromise

  await synthetics.executeStep('Click "Logout" button', async function() {
    await page.waitForSelector('.container > .row > .col-md-5 > .d-flex > a')
    await page.click('.container > .row > .col-md-5 > .d-flex > a')
  })

  await navigationPromise

};
exports.handler = async () => {
    return await recordedScript();
};