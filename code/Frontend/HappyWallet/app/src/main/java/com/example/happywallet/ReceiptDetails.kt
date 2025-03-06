package com.example.happywallet

//Imports
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.happywallet.api.*
import com.example.happywallet.models.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.happywallet.navigation.Screen
import com.skydoves.landscapist.glide.GlideImage
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//Main UI for receipts details which shows all of the receipts
@Composable
fun ReceiptDetailsPage(navController: NavController) {
    var receipts by remember { mutableStateOf<List<Receipt>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    //Calls the getReceipt function to GET all of the receipts stored in the database
    LaunchedEffect(Unit) {
        val apiService = RetrofitInstance.getApiInstance(context)
        apiService.getReceipt().enqueue(object : Callback<List<Receipt>> {
            override fun onResponse(call: Call<List<Receipt>>, response: Response<List<Receipt>>) {
                isLoading = false
                receipts = response.body() ?: emptyList()
            }

            override fun onFailure(call: Call<List<Receipt>>, t: Throwable) {
                isLoading = false
                errorMessage = "Network error: ${t.message}"
            }
        })
    }

    val availableCategories = receipts.mapNotNull { it.receiptCategory }.distinct().sorted()

    //Shows only the receipts of the selected category if a category is selected, otherwise shows all receipts
    val filteredReceipts = if (selectedCategory.isNullOrEmpty()) {
        receipts
    } else {
        receipts.filter { it.receiptCategory == selectedCategory }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(4.dp)
    ) {
        IconButton(
            onClick = { navController.navigate(Screen.Main.route) },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(36.dp)
                .padding(4.dp)
        ){
            Icon(
                Icons.Default.Home,
                contentDescription = "Home",
                tint = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = "Receipts", style = MaterialTheme.typography.displayMedium)
            Spacer(modifier = Modifier.height(16.dp))

            //Calls the CategoryDropdownMenu to allow the user to select a specific category of receipts to see
            if (availableCategories.isNotEmpty()) {
                CategoryDropdownMenu(
                    categories = availableCategories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            when {
                isLoading -> {
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Unknown error",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                receipts.isEmpty() -> {
                    Text(
                        text = "No receipts available. Add a new one!",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredReceipts) { receipt ->
                            ExpandableReceiptCard(receipt)
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.navigate(Screen.Scanner.route) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)

            ) {
                Text("Scan a Receipt")
            }
        }
    }
}

//Function to show each receipt in an expandable card
@Composable
fun ExpandableReceiptCard(receipt: Receipt) {
    var expanded by remember { mutableStateOf(false) }
    var showImage by remember { mutableStateOf(false) }

    //Handles the rotation of the arrow icon
    val rotation by animateFloatAsState(if (expanded) 180f else 0f, label = "Arrow Rotation")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .animateContentSize()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Merchant")
                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = receipt.merchant ?: "Unknown Merchant",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Total: ${receipt.totalAmount ?: 0.00}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = "Uploaded: ${receipt.uploadedAt}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Category: ${receipt.receiptCategory ?: "Uncategorized"}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand",
                    modifier = Modifier
                        .size(42.dp)
                        .graphicsLayer(rotationZ = rotation)
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showImage = !showImage }
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (showImage) "Hide Receipt" else "View Receipt",
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                if (showImage) {
                    Spacer(modifier = Modifier.height(8.dp))
                    ReceiptImage(url = receipt.imageUrl ?: "")
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Items:",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                receipt.parsedItems?.forEach { item ->
                    val description = item.description.value
                    val price = item.total_price.value.toDoubleOrNull()?.let { String.format("%.2f", it) } ?: "0.00"

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = description, style = MaterialTheme.typography.bodyMedium)
                        Text(text = price, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

//UI for a single receipts, this is shown after a user uploads an image to the server
@Composable
fun SingleReceiptDetailPage(receiptId: String, navController: NavHostController) {
    var receipt by remember { mutableStateOf<Receipt?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val categories = listOf(
        "Meal", "Supplies", "Hotel", "Fuel", "Transportation", "Communication", "Subscriptions", "Entertainment", "Training", "Healthcare", "Other"
    )

    //Calls getReceipt to get all receipts and filters the receipt that was just taken, by ID when the page loads
    LaunchedEffect(receiptId) {
        val apiService = RetrofitInstance.getApiInstance(context)
        apiService.getReceipt().enqueue(object : Callback<List<Receipt>> {
            override fun onResponse(call: Call<List<Receipt>>, response: Response<List<Receipt>>) {
                val allReceipts = response.body() ?: emptyList()
                receipt = allReceipts.find { it.id.toString() == receiptId }
                selectedCategory = receipt?.receiptCategory
            }

            override fun onFailure(call: Call<List<Receipt>>, t: Throwable) {
                errorMessage = "Network error: ${t.message}"
            }
        })
    }

    //Function to allow user to update the category of the receipt if needed
    fun updateCategory(newCategory: String) {
        val apiService = RetrofitInstance.getApiInstance(context)
        val patchData = mapOf("receipt_category" to newCategory)
        isLoading = true

        //Calls the updateReceipt function which does a PATCH request to change the predefined category
        apiService.updateReceipt(receiptId, patchData).enqueue(object : Callback<Receipt> {
            override fun onResponse(call: Call<Receipt>, response: Response<Receipt>) {
                isLoading = false
                receipt = response.body()
                selectedCategory = newCategory
                isEditing = false
            }

            override fun onFailure(call: Call<Receipt>, t: Throwable) {
                isLoading = false
                errorMessage = "Network error: ${t.message}"
            }
        })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Text(
                        text = "Receipt Details",
                        style = MaterialTheme.typography.displayMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

                    )
                }

                when {
                    receipt == null && errorMessage == null -> {
                        item { CircularProgressIndicator(modifier = Modifier.padding(16.dp)) }
                    }

                    errorMessage != null -> {
                        item {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    else -> {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(text = "Merchant: ${receipt?.merchant ?: "Unknown"}", style = MaterialTheme.typography.titleLarge)
                                    Text(text = "Total Amount: ${receipt?.totalAmount ?: "N/A"}", style = MaterialTheme.typography.titleMedium)
                                    Text(text = "Uploaded: ${receipt?.uploadedAt ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)

                                    Spacer(modifier = Modifier.height(16.dp))

                                    receipt?.imageUrl?.let { imageUrl ->
                                        ReceiptImage(url = imageUrl)
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(text = "Items:", style = MaterialTheme.typography.titleMedium)
                                    Column(modifier = Modifier.fillMaxWidth()) {
                                        receipt?.parsedItems?.forEach { item ->
                                            Text(
                                                text = "• ${item.description.value}: ${item.total_price.value}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(text = "Category:", style = MaterialTheme.typography.titleMedium)

                                    if (isEditing) {
                                        var expanded by remember { mutableStateOf(false) }

                                        Box(modifier = Modifier.fillMaxWidth()) {
                                            OutlinedButton(
                                                onClick = { expanded = true },
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Text(text = selectedCategory ?: "Select Category")
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                                            }

                                            DropdownMenu(
                                                expanded = expanded,
                                                onDismissRequest = { expanded = false },
                                                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                                            ) {
                                                categories.forEach { category ->
                                                    DropdownMenuItem(
                                                        text = { Text(category) },
                                                        onClick = {
                                                            selectedCategory = category
                                                            expanded = false
                                                        }
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Button(
                                            onClick = { selectedCategory?.let { updateCategory(it) } },
                                            enabled = !isLoading,
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary,
                                                contentColor = Color.White
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            if (isLoading) {
                                                CircularProgressIndicator(
                                                    color = Color.White,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            } else {
                                                Text("Update Category")
                                            }
                                        }
                                    } else {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = selectedCategory ?: "N/A", style = MaterialTheme.typography.bodyMedium)
                                            IconButton(onClick = { isEditing = true }) {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit Category")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            //Button to navigate to all receipts page when done viewing the receipt
            Button(
                onClick = { navController.navigate(Screen.Details.route) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("View All Receipts")
            }
        }
    }
}

//Loads and displays the image, uses Glide
@Composable
fun ReceiptImage(url: String) {
    GlideImage(
        imageModel = { url },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .aspectRatio(1f),

        //Show loading circle to show that image is loading
        loading = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    )
}

//Function for the category dropdown menu
@Composable
fun CategoryDropdownMenu(categories: List<String>, selectedCategory: String?, onCategorySelected: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedCategory ?: "Select Category")
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
        }

        //Dropdown menu with the categories of the receipts that are available
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            DropdownMenuItem(
                text = { Text("All Categories") },
                onClick = {
                    onCategorySelected(null)
                    expanded = false
                }
            )
            categories.forEach { category ->
                DropdownMenuItem(
                    text = { Text(category) },
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    }
                )
            }
        }
    }
}