## HappyWallet
HappyWallet is an android mobile application which is designed to help users manage their finances efficiently with the use of receipts. The user will be able to take a picture of their receipt and the system will process and extract relevant data, such as the total or the supposed category of the receipt, from the receipt and present it to the user. The receipt will then be stored and the user will be able to look at all the receipts. The user will also be able to create budgets, such as shopping or holiday budgets, and scanned receipts will automatically be placed inside of these budgets based on the start and end date of the budget, and if there any category filters on the budget as well. The system will also allow users to create a report of the budget, showcasing spending breakdown of different categories and expenses, which is also showcased on a pie chart. The report will also be able to be exported into a nicely formatted excel (.xlsx) file. This app assumes that the receipts are in english and only works with euros.
The system is developed using Django as the backend and Kotlin as the frontend. The database that is being used is PostgreSQL. Both the backend and the database are hosted on Azure Cloud.

## Main Page

  

  

Upon logging in the user is presented with the main home page of the application where they have a couple of options.

  

  

- The ” Receipt Scanner” button will navigate the use to the receipt scanner page where they will be able to scan a receipt.

  

- The “Receipt Details” button will navigate to the receipts page where all of the user’s scanned receipts will be displayed.

  

- The “View Budgets” button will navigate to the budgets page where all of the user’s budgets will be displayed

  

- The “Logout” button will log the user out of their account and navigate to the login page.

  

  

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXf52YltTnoVMOwCzizhvAvSso_Ijn-JHJ4rDTakKI0mropSHkygEozPhOqG5cJCr4OICz54tIs-f9viZFqO_G6ND2LOwkuJFqfDJqJuwvdVQ5HNWoWLsb2ThQa3Fx8V50tHwBUFoQ?key=8-_zyROPyDLur8Ggdi-BX538)

  

  

---

  

## Receipt Scanner Page

  

  

The receipt scanner page is where the user will be able to take a picture of the receipt that they want to upload into the app.

  

  

At first the user is presented with a “Take a Picture of the Receipt” button. Upon clicking the button their camera will automatically open, allowing them to take a picture of the receipt, using the flash if necessary. After the user is takes the picture, they will be shown a preview of the image that they took and will be presented with a “Upload Image” button, this will allow them to upload the image to the server which will process the receipt and return all of the key information in a receipt page.

  

  

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXc5IL7p9wI_wRJ0SCryYhcKbqZ4giI9C9xERXUisdmwP346vwc57OuQ1oRHS4A9ieDVmahp8KrhS_L4YKEmqM1zoF2c9kVlEVWFQpiM9BGIvG0ESI7WixgByGfglQV9FOIZW4PRkQ?key=8-_zyROPyDLur8Ggdi-BX538)

  

  

In this receipt page the user will be able to see all of the key information about the receipt and will be shown a category that has been categorised by the server.

  

  

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXedGhd5S6cHAZKUekLpADB53TcLhCFrSNm8opegPg5eLaBHkshUeDSsFWawZD4us9WEH1dKbmXFPpBDoibaja0yDU5-TWujfq6CyGhn-AbiLWooqtq_ggcA7JJw0mVu_6XzE2ZP?key=8-_zyROPyDLur8Ggdi-BX538)

  

  

If the user thinks this is incorrect they can click the edit button which will present the user with a drop-down menu where they will be able to choose from a list of categories to assess which one is the most appropriate. After the user has made their decision they can press the edit category button.

  

  

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXeJ0er0rHLiJlxgM7GZfL3oX7wTnJv1eFR_LeGHDgUrNBrTiHy3gZblOgYl7eU75hpkZcgckKWi7T_ZeRllOPWJQnu_HkuB6rXg-sS2NchvQ4ur3_nPp-pmmzk6O1bL7nyAWq_K4g?key=8-_zyROPyDLur8Ggdi-BX538)

  

  

---

  

## Receipts Page

  

The receipts page displays all of the user’s scanned receipts. At first each receipt’s key information such as “Merchant” and “Total Amount” is displayed, the user can then click on the receipt they want to expand to reveal more information such as the price of each item. Here the user can also click the “View Receipt” button to show the picture they took of the receipt, after viewing they can click the button again to hide it.

  

  

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdaPYpCHUpRRVju4haeWlABGSUdFy4qsXwxss-dpoxJnIy0GEi5aPuft7XHV6Q2B9CaGMDcmsSeB0I7pcIHS-kOerrWcZR0e6OsK0Qq65y6E0LxneBvQKjfPoIfQDCKsgh1aE-S?key=8-_zyROPyDLur8Ggdi-BX538)

  

  

The receipts page also has a category filter at the top of the screen, when clicked it will produce a dropdown menu of all of the available receipt categories that the user has scanned, this will allow the user to select specific categories of receipt they want to view. There is also a “Scan a Receipt” button at the bottom of the page to navigate the user directly to the scanner page.

  

  

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXcFJNHHGVFEf-EwoSA1a_nL2e207lMAgmusutyHLSNTVt_FtVbSnbslQoAl4r9iKfAQzCWpElyFov9sxINYrQaONdZtparN5KPD6OPuxLHGBoyX0T0SR0JMzibL6Rn7Sex4VYd7XA?key=8-_zyROPyDLur8Ggdi-BX538)

  

  

---

  

## Budgets Page

  

The budgets page displays all of the user’s created budgets. If there are no receipts a message is displayed to the user. There is a button at the bottom of the page which will navigate the user to the create budget page where the user will be able to create a budget.

  

  

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXewfl1J1KaKDmKRiH3LsAsANlRyZbnqK_s8XVbImk9YnncrDeHf4LqWO_llvMQp3EgYnuUIlnDQcgEbnZnx_aU7cxDqWyN6WXYFQpm09hqu4ulVYSnCFAiX3fbRtYXNgaSiEs3SgA?key=8-_zyROPyDLur8Ggdi-BX538)

  

  

Each budget displayed in the page can be clicked to display information about the budget as well as the receipts that are associated with the budget. There is also a red bin icon, pressing this icon will present the user with a confirmation window asking if they want to delete the budget, if the user presses Delete the budget will be deleted. In the budget details page the user is presented with two buttons:

  

- “View Budget Report” button navigates the user to the budget report page

  

- “Back to Budgets” button navigates the user back to the budgets page

  

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXcIZW03Z2x5qhJy-Pbx1QgPtmVYhwX0_E79n9IgqwLgCDXc67rWEwXwbWYJcLCLcYAYNWRvyruowR6cpS9kOJPKEhEpQlTqeWuXgqttNnn7swFOCeh8vYBV6Kv6DgsVWA2vWtL7Yg?key=8-_zyROPyDLur8Ggdi-BX538)

  

  

If the current spending is more than the limit amount of the budget, a red warning icon will be displayed on the budget in the budgets page, and a red message will appear in the budget details page notifying the user that they are over the budget limit

  

  

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXcsBNOLdPr5OUmB1UfFAwqJVYCL1XeOZ6PKpLrtX63eeJPsncx8N5mfykUs_DpPBRgBYTz5_2KzrIgEYbOBJ3jeqzhXImogDKz1CgtDRg8nOW6b_VaGO2-G5u_W8ZgePqcMZFAENA?key=8-_zyROPyDLur8Ggdi-BX538)

  

  

---

  

## Create Budget Page

  

The create budget page allows the user to create a budget. They will be able to enter a name of the budget, the budget amount, the categories of the budget, and the start and end dates. Once these have been entered the user can press the “Save Budget” to save the budget and be navigated back to the budgets page.

  

  

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXeL9-pnm75_4ugYSRhpxEiJiEQEaGY1IUF_S-ZbwbQUo74N9dOPMhcixW5OsJTHw_9ePw9db_uzpgK9mUse38kUM-YTgX446E3Y_sxt1uanaHd7ymVr6rx66wHdTxxwrqgkzCgt?key=8-_zyROPyDLur8Ggdi-BX538)

  

  

---

  

## Budget Report Page

  

The budget report page contains additional information about the budget and a breakdown of the spending. The user is presented with a pie chart which shows the different categories and how much of the current spending each category takes up. There is also two expandable sections:

  

  

- “Budget Summary” which shows the time period, time limit and total spent. The total spend is displayed in green if below the limit and red if exceeding the limit.

  

- “Spending Breakdown” which show the total amount and number of items for each category of receipts in the budget.

  

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXd1r5NpSJ6BX-edaoFtg2Fq5P6Hlc0HuSiwAHrD_KaVRl-hTHx-g_AUBU6Dj26Lew_faAuvSfNLQRcvwRPJDNDY2Z7UoLUZ5i0xpPRICyNVEAg7ce9mIavfp5ouR0Wr_E80Cusfiw?key=8-_zyROPyDLur8Ggdi-BX538)

  

  

At the bottom of the page there is also a “Download Report (Excel)” button which allow the user to download the report. The button press will start the download process, once the download is complete the user will receive a push notification which will open the excel report.

  

  

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdG-DoFYyiEWrqtix96APXj0ul8nx6oilyd7Ce3IH5Of1NFE1kesMRVntPM4e9Fc6lpVTkx1g6DqVYQQaKr7HDe3rsT7SDSCqqh_eHFbBF7D6SxjhzyUDHaSaCgBPKMiI2drx06xA?key=8-_zyROPyDLur8Ggdi-BX538)

  

  

![](https://lh7-rt.googleusercontent.com/docsz/AD_4nXda8ueJhn2t2Naq841mR4XRAQI8n4BALp2z7zueQIQPdtPALoOA_0UDPwXLN3B0GGC7R3Ihp90b4pbzNfFhHwlI2w_c-3flUyupvX2uJVcHJL1YEwKnFeZcXIK1EwAX8O4OMnxvQA?key=8-_zyROPyDLur8Ggdi-BX538)
#
## Installation Guide
**Step 1: Clone the Repository**

  

Open a terminal/command prompt and navigate to the directory where you plan to clone the repository. Clone the repository with this command:

  

```

git clone [https://github.com/mbazaras7/HappyWallet.git](https://github.com/mbazaras7/HappyWallet.git)

```

**Step 2: Locate the APK**

  

After cloning the repository, navigate to the releases folder, “HappyWallet/releases”. The APK is situated in this folder.

  

**Step 3: Install the APK to Your Android Device**

  

There are a few way to do this:

  

**1.** Connect your Android device to your computer with a USB cable and copy the APK file from the releases folder to your device’s storage. Then find where you put the APK in your device and click on it.

**2.** Use the link in the “LinkToDriveAPK” file in the releases folder to access the Google Drive folder on your device. Then click the apk, and then Package Installer to start the download process. You might be prompted to change the “Install from Unknown Source” setting to proceed with the download.

**3.** Open the repository on Android Studios, then run the “app” with your phone plugged into the computer. This will install the program onto your device automatically. However some additional settings might need to be changed in the “Developer Options” for this option to work.

For options **1** & **2**, you may be prompted with a popup “Install from Unknown Sources”, you will need to go to your devices Settings -> Security and enable “Install from Unknown Sources”.

  

**Step 4: Launch the App**

  

After the installation is complete, you can find the HappyWallet app on your homescreen or app drawer. You can now launch the app, the backend is set up on the cloud so no backend installation is required to use all of the features on the app.