/*
 * A class to trace back an endorsement chain across platforms
 */

class ChainCrawler {
    async crawlPossessionChain(initPossessionTdtUrl) {
        let possessionChain = [];
        let platformChain = [];
        let titleHolderChain = [];
        const url = new URL(initPossessionTdtUrl);
        let serverName =  url.host;
        const apiPath = (url.pathname).match(/.*\//)[0];
        let currentPossessionUrl = initPossessionTdtUrl;
        let currentTdt;
        do {
            currentTdt= await (await fetch(currentPossessionUrl)).json();
            possessionChain.push(currentTdt.transferBlock);
            platformChain.push(serverName);
            const possessionBlock = (new PossessionTransferBlock(JSON.parse(currentTdt.transferBlock)))
            const previousRegistryURL = possessionBlock.blockPayloadAsJson()["previousRegistryURL"]; //note: only non-null if block is an import block
            if (previousRegistryURL) {
                serverName = (new URL("https://"+previousRegistryURL)).host;
            }
            currentPossessionUrl = "https://" + serverName + apiPath + currentTdt.previousTransferBlockHash;
            let currentTitleUrl = "https://" + serverName + apiPath + possessionBlock.titleTransferBlockHash();
            let currentTitleTdt= await (await fetch(currentTitleUrl)).json();
            titleHolderChain.push(currentTitleTdt.transferBlock);
        } while (currentTdt.previousTransferBlockHash != null);
        return {"possessionChain": possessionChain, "platformChain": platformChain, "titleHolderChain": titleHolderChain};
    }
}
