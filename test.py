def format_scanresult(scanresult):
    for i in range(len(scanresult)):
        if len(scanresult[i]) < 3:
            while len(scanresult[i]) < 4:
                scanresult[i].append("No data")
        if len(scanresult[i]) == 3:
            scanresult[i].append(scanresult[i][2])  # Move the third to the fourth
            scanresult[i][2] = "No data"

    return scanresult

# Example usage
scanresult = [
    ["https://testtest.com", "[400]", "Nginx,OpenResty:1.13.6.1"],
    ["https://testtest.com", "[400]"],
    ["https://testtest.com"]
]

formatted_result = format_scanresult(scanresult)
for result in formatted_result:
    print(result)