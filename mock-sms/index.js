const express = require('express');
const app = express();

app.use(express.urlencoded({ extended: true }));

let smsCount = 0;

app.post('/sms', (req, res) => {
    const phone = req.query.phone;
    const { message } = req.body;
    smsCount++;
    console.log(`${message}`);
    console.log(`[SMS #${smsCount}] 📩 ${phone}:`);
    res.json({ result: 'OK' });
});

app.listen(8082, () => console.log('🟢 SMS mock server listening on 8082'));
