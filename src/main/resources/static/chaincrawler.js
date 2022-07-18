/*
 * A class to trace back an endorsement chain across platforms
 */

class ChainCrawler {
    async crawlPossessionChain(initPossessionTdtUrl) {
        let possessionChain = [];
        let platformChain = [];
        let titleHolderChain = [];
        let possessorPlatformChain = [];
        let titleHolderPlatformChain = [];
        const url = new URL(initPossessionTdtUrl);
        let serverName = url.host;
        const apiPath = (url.pathname).match(/.*\//)[0];
        let currentPossessionUrl = initPossessionTdtUrl;
        let possessionBlock;
        do {
            const currentTdt= await (await fetch(currentPossessionUrl)).json();
            platformChain.push(serverName);
            possessionBlock = (new PossessionTransferBlock(currentTdt.transferBlock));
            possessionChain.push(possessionBlock);
            const previousRegistryURL = possessionBlock.blockPayloadAsJson()["previousRegistryURL"]; //note: only non-null if block is an import block
            if (previousRegistryURL) {
                serverName = (new URL(previousRegistryURL).host);
            }
            currentPossessionUrl = "https://" + serverName + apiPath + possessionBlock.previousBlockHash();
            let currentTitleUrl = "https://" + serverName + apiPath + possessionBlock.titleTransferBlockHash();
            let currentTitleTdt= await (await fetch(currentTitleUrl)).json();
            const currentTitleTransferBlock = new TitleTransferBlock(currentTitleTdt.transferBlock);
            titleHolderChain.push(currentTitleTransferBlock);
            titleHolderPlatformChain.push(currentTitleTransferBlock.titleHolderPlatform());
        } while (possessionBlock.previousBlockHash() != null);

        const nbBlocks = platformChain.length;
        let currentTitleHolder = titleHolderChain[nbBlocks-1]; //first title holder and platform (last items of their resp. arrays)
        let currentTitlePlatform = platformChain[nbBlocks-1];
        let titlePlatformChain = [currentTitlePlatform];
        const initialPossessorPlatformPossiblyExport = possessionChain[nbBlocks - 1].blockPayloadAsJson()["nextRegistryHost"];
        let currentPossessorPlatform = initialPossessorPlatformPossiblyExport ? initialPossessorPlatformPossiblyExport : platformChain[nbBlocks - 1];
        possessorPlatformChain = [currentPossessorPlatform];
        for (let i = nbBlocks-2; i >=0; --i) {
            if(currentTitleHolder != titleHolderChain[i]) {
                currentTitleHolder = titleHolderChain[i];
                currentTitlePlatform = platformChain[i];
            }
            titlePlatformChain.splice(0, 0, currentTitlePlatform);
            const nextRegistryHost = possessionChain[i].blockPayloadAsJson()["nextRegistryHost"]; //note: only non-null if block is an export block
            if (nextRegistryHost) {
                currentPossessorPlatform = nextRegistryHost;
            }
            possessorPlatformChain.splice(0, 0, currentPossessorPlatform);
        }

        let statuses = new Array(possessionChain.length);
        statuses[statuses.length-1] = "ISSU";
        for (let i = 1; i < statuses.length-1; ++i) {
          if(possessionChain[i].titleTransferBlockHash() != possessionChain[i-1].titleTransferBlockHash()) {
            statuses[i] = "ENOR";
          } else {
            statuses[i] = "POSS";
          }
        }
        let latestTransfereeJWK = possessionChain[0].transferee();
        if(possessionChain[statuses.length-1].verifyNth(0, KEYUTIL.getKey(latestTransfereeJWK), {alg: ['RS256']})) {
          statuses[0] = "SURR";
        } else {
          statuses[0] = "POSS";
        }

        return {"possessionChain": possessionChain, "platformChain": platformChain,
                "titleHolderChain": titleHolderChain, "titlePlatformChain": titlePlatformChain,
                "possessorPlatformChain": possessorPlatformChain, "titleHolderPlatformChain": titleHolderPlatformChain,
                "statuses": statuses}
    }

    async chainToThumbprints(promiseChain) {
        const awaitedChain = await Promise.all(
            promiseChain.map(function(transferBlock) {
                return transferBlock.transfereeThumbprint();
            })
        );
        return awaitedChain;
    }

    async chainToTimestamps(promiseChain) {
        const awaitedChain = await Promise.all(
            promiseChain.map(function(transferBlock) {
                return transferBlock.timestamp();
            })
        );
        return awaitedChain;
    }
}
