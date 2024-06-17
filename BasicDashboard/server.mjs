import express from 'express';
import fetch from 'node-fetch';
import cors from 'cors';
const app = express();
const PORT = 3000;

app.use(cors());

app.get('/', async (req, res) => {
    try {
        const query = {
            "query":
                `
                SELECT *
                FROM

                (SELECT *, CURRENT_TIMESTAMP AS "current"
                FROM "flinkoutput"
                WHERE customer = 'mans' AND vehicle = 'FT-GUID-00' AND signal = 'engineOilTemp' AND granularity = 'min'
                ORDER BY 1 DESC
                LIMIT 1)

                UNION ALL

                (SELECT *, CURRENT_TIMESTAMP AS "current"
                FROM "flinkoutput"
                WHERE customer = 'mans' AND vehicle = 'FT-GUID-00' AND signal = 'engineOilTemp' AND granularity = 'hour'
                ORDER BY 1 DESC
                LIMIT 1)
                `,
            "context": { "useCache": false }
        };

        const druidResponse = await fetch('http://192.168.0.174:8888/druid/v2/sql', {
            method: 'POST',
            body: JSON.stringify(query),
            headers: { 'Content-Type': 'application/json' },
        });

        // .json() gets the payload body
        // response has structure [ { minFreq: 1, Count: 21 } ], array of objects
        const jsonData = await druidResponse.json();
        console.log(jsonData)
        res.json(jsonData);

    } catch (error) {
        console.error('Error querying Druid:', error);
        res.status(500).json({ error: 'Failed to fetch data from Druid' });
    }
});

app.listen(PORT, () => console.log(`Server running on http://localhost:${PORT}`));

