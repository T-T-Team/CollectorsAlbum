const fs = require("fs");

const cards = fs.readdirSync("./cards");
const groupMode = {
    key: "category",
    value: "collectorsalbum:items"
};

const intermediate = [];
cards.forEach(card => {
    const cardData = require(`./cards/${card}`);
    const att = cardData[groupMode.key];
    if (!att || att !== groupMode.value)
        return;
    intermediate.push({id: cardData.id, number: cardData.number});
});

fs.writeFileSync("./out.json", JSON.stringify(intermediate.sort((a, b) => a.number - b.number).map(a => a.id), null, 2));