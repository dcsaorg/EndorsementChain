describe("Chaincrawler", function() {
    it("should properly convert a possession chain to thumbprints", async function() {
        let chainCrawler = new ChainCrawler();
        let possessionChain = [ new PossessionTransferBlock(
            {"payload":"eyJ0cmFuc2ZlcmVlIjp7Imt0eSI6IlJTQSIsIm4iOiJ4cEZzaXhpZHgxMnUzc0I2WnBRWW44SWN1SVlqQmZHTTBSbHUzVG9vWkhPa0E1SGJwdlJ2c2FqX0hUVktDQ1FkdzFQcUowUy1jYVpERGpDV3lHT1hoRGZ4UDBKMDRDM1UyMWsyS3d5Uy02YkJoLThGVkFaU2p6R2lrWC1aT1ozNVJ5a0xGOFl5cU80LVIxdDhweEFuTkxiTkpDUjNlelROMkxFY3dVZmpZYzZvaGdJZThLaUJiVU9fSjNUQXIwWG9mdGtDZlZPMHd1ZkZyNTlCWUYwUWl3MnkyWng4RmRqclVYMHlncVJ3a1M4U1Y5SXZmVUpiTGo1b09LMjFreVZmeGhTaEkyU1FnaWQ4clhfSzlrM2RNTnVrYTRDTUFyT0dNMVl0cHVNOXE1ZjFWcDNsd1NicEQ3RW8taTRMZjJrbkNxSlF6Z0daR1p5MmNDUlVxMV82d1EiLCJlIjoiQVFBQiJ9LCJibG9ja1BheWxvYWQiOnsidGl0bGVUcmFuc2ZlckJsb2NrSGFzaCI6IjQ4MjI5ZDgzNTc4NTRiZjY0NWJiMzZlMDY5MTM3YTA2MTM4ZWRmN2Q3ZDgzODBjMzI4Y2ZjNTI2MmEwMWU5YWMifX0","signatures":[{"protected":"eyJhbGciOiJSUzI1NiJ9","signature":"WDWvB31FMWmF9r-bR7sOU3J7SEbs_tawSgqs4sNnAmoHhfsar8tur8gJtKn0-n_aAqehvgOQ1FJDAteiZdyElJTOXncyGzmUFzZCSKwnI3gksHE4i4EdEPZrDFqB374VgaDtAkoupCjDJRZ2J6d7E5Lb3WGoK8WvXgT6vZff7cKAjkl60cW7TqEDQ5H2w6T0Vsdv_u7oMXhT6Z5uAddlBpsU7MJK1mBo13IIRHbH47uiJHqU_pl3n5zV7QnTPKx9l7iIBfVc7H4jUYH3sTRt2T39jHNE_ZevLIOcGnrLYMZ7Lg1PTWS6h3moVL4l_eIVmA3PzDltYJMComEbRhWDoA"}]}),
        new PossessionTransferBlock(
            {"payload":"eyJ0cmFuc2ZlcmVlIjp7Imt0eSI6IlJTQSIsIm4iOiJwMkxJVlB1dzFfdlVnaElvT0RaR0NNZThVWXB0QUFYcTdicE81bERLTUlTUl9UZ01aaC1INjVOQU9uTUJlOEVZbldwQi1yYXRJTHU4WGVORVR4Z1JSUjFCUlVmWE5pTXFDMXhtQXgycEdsb0pJb25DUmJDX1Jnc3V5VGg5cHRMZUdPbVFQMWh0LTctZjRzWk93TE5YR2U1U0xWdjBFY1QzWEJDbkhNWEJxTVZEZmpEODA1NmQ0YXlHOE9XeUQwMURMUVNFUEhYeWt4LXQ1Wk1rN1lSdEV3NE9UYzFrVFRaSThlOGZtSEZKZWlpVmJsR1cxWlZKbUUyTUtxTXk0VVpIMkVBN3lHRUJPYUpVZEMwNjlTdTZub0YyV3MxaDBtVC0zNHZhNmhRU1JPeXp2MWxnZGF5MC13dnItN01xLVJ4bEwzRXE2M1RpNEMtVEoxRk1vUnQ5clEiLCJlIjoiQVFBQiJ9LCJibG9ja1BheWxvYWQiOnsidGl0bGVUcmFuc2ZlckJsb2NrSGFzaCI6IjQ4MjI5ZDgzNTc4NTRiZjY0NWJiMzZlMDY5MTM3YTA2MTM4ZWRmN2Q3ZDgzODBjMzI4Y2ZjNTI2MmEwMWU5YWMiLCJwcmV2aW91c1JlZ2lzdHJ5VVJMIjoibG9jYWxob3N0Ojg0NDMvYXBpL3YxL3RyYW5zcG9ydC1kb2N1bWVudC10cmFuc2ZlcnMvZTkyYzI4NjM5YTc0OWJhNzE3YTdjMmRhMjgxYzhkZGEwOGRiZTAxMjEwNDdmOTE0MmVjZWMyN2QyMzRiMWFkZCJ9fQ","signatures":[{"protected":"eyJhbGciOiJSUzI1NiJ9","signature":"VmFKZ72I1bJcW-hRx7S0M8Me2ap8dmeKZoxOD3V7yvD3E0b2VWe166E7FqqeLkP43SMMy9DV5w3jykMi3BAxZWBKDtCN2HGvjnBNCU3OzSyQDaSKti8lTbNQA-IIvtW6Yk8ywGbA-EzxLxnwgespAhZYIDB2AFTfvROBWqU2Hlv8a2Xan_AiMECUYWKt7p3U4OWS7P85UgXey7BiqEjL3FyzPsGn0Kr-yFZ99xaKlK95j3JTm-P58TwhOTl6rUUMnxn9vRWZ-QWXCMwpnfK82UiSw6ocNOLuxMBDY19Ns1gKdfePjpWloD5xjfzHsJy4h0Bd4p0o_3ZPQ5shHHk8vw"}]})
        ]
        const thumbprintChain = await chainCrawler.chainToThumbprints(possessionChain);
        expect(thumbprintChain[0]).toEqual("19315844aec2782bb18b");
        expect(thumbprintChain[1]).toEqual("a162bc5f6402209fa797");
    });

    it("should properly trace back the testchain", async function() {
        const chainStart = "https://localhost:8443/spec/testchain/10098707e7a92f3fb7b09baeb9adb7eb3d5693447aea59581f4a835ecccb4580"
        let chainCrawler = new ChainCrawler();
        const chains = await chainCrawler.crawlPossessionChain(chainStart);
        expect(chains.possessionChain.length).toEqual(3);
    });
});
