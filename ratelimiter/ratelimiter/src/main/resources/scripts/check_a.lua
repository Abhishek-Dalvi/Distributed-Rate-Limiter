local key = KEYS[1]
local timestamp = ARGV[1]
local bucketCapacity = ARGV[2]
local bucketRefillTime = ARGV[3]
local bucketKeyExpirationTime = ARGV[4]
local value = redis.call('GET', key)


-- Creating a new key with value "5,timestamp" if it does not exist (Bucket initialization)
if not value then
    -- concatenate with a comma
    local valueString = bucketCapacity .. "," .. tostring(timestamp)
    redis.call('SETEX', key, tonumber(bucketKeyExpirationTime), valueString) -- 1 hour expiration
end

local value = redis.call('GET', key)

local delimiter = ","
local parts = {}
for part in string.gmatch(value, "[^" .. delimiter .. "]+") do
    table.insert(parts, part)
end


-- Refilling the bucket if the time difference is greater than or equal to 10 seconds (10000 milliseconds)
local currentTime = tonumber(timestamp)
local currentTokenNumber = tonumber(parts[1])
local lastTimestamp = tonumber(parts[2])
local bucketRefillTimeNum = tonumber(bucketRefillTime)
local elapsedTime = currentTime - lastTimestamp
local effectiveTokenAsPerRate = 0

if elapsedTime >= bucketRefillTimeNum then
    effectiveTokenAsPerRate = currentTokenNumber + (elapsedTime / bucketRefillTimeNum)
    -- redis.log(redis.LOG_NOTICE, "Debug: effectiveTokenAsPerRate=" .. effectiveTokenAsPerRate)
    currentTokenNumber = math.min(effectiveTokenAsPerRate, tonumber(bucketCapacity)) -- Assuming the bucket capacity is 5
    lastTimestamp = currentTime
end


-- Checking if there are tokens available in the bucket
if currentTokenNumber >= 1 then
    currentTokenNumber = currentTokenNumber - 1
    -- Update the bucket with the new token count and timestamp
    local newValueString = tostring(currentTokenNumber) .. delimiter .. tostring(lastTimestamp)
    redis.call('SETEX', key, tonumber(bucketKeyExpirationTime), newValueString) -- 1 hour expiration
    return {true, currentTokenNumber, "Your request is successfull for userId: " .. key}
else
    return {false, currentTokenNumber, "Token count exhaust, Wait for 10 second for next request for userId: " .. key}
end


